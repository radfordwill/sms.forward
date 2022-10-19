package radwil.sms.utils

import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import radwil.sms.App
import java.io.*
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext


private const val TIMEOUT = 15000
private const val APPLICATION_NAME = "Email Extractor"
private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
private val tokenFolder = Constants().TOKENFOLDER
private val appFolder = Constants().APPLICATIONFOLDER
private val SCOPES = setOf(
        GmailScopes.GMAIL_LABELS,
        GmailScopes.GMAIL_READONLY,
        GmailScopes.GMAIL_METADATA
)

class GmailUtils {

    private val maxFetchthreads = Runtime.getRuntime().availableProcessors()

    private val executors: ExecutorService = Executors.newFixedThreadPool(maxFetchthreads)

    private val dispatcher = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            executors.execute(block)
        }
    }

    /**
     * Creates an authorized Credential object for browser intent.
     * @param this@getCredentials The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws java.io.IOException If the credentials.json file cannot be found.
     */
    private fun NetHttpTransport.getCredentials(): Credential? {

        val inputStream = getCredentialsAsInputStream()
        val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

        val authorizationFlow: GoogleAuthorizationCodeFlow = getAuthorizationFlow(clientSecrets)

        val ab: AuthorizationCodeInstalledApp =
            object : AuthorizationCodeInstalledApp(authorizationFlow, LocalServerReceiver()) {
                @Throws(IOException::class)
                override fun onAuthorization(authorizationUrl: AuthorizationCodeRequestUrl) {
                    val url = authorizationUrl.build()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    App.instance.startActivity(browserIntent)
                }
            }
        return ab.authorize("user").setAccessToken("user")
    }

    /**
     * Gets the /credentials.json file
     * @return InputStream
     */
    private fun getCredentialsAsInputStream(): InputStream {
        try {
            return File("$appFolder/credentials.json").inputStream()
        }catch (e: FileNotFoundException) {
            throw FileNotFoundException("Resource Not found: $appFolder/credentials.json")
        }
    }

    /**
     * Gets authorisation flow so that the application can authenticate into Google Calendars
     * @param this@getAuthorizationFlow allows the app to use HTTP connections
     * @param clientSecrets secrets for authentication into Google
     * @return GoogleAuthorizationCodeFlow
     */
    private fun NetHttpTransport.getAuthorizationFlow(
        clientSecrets: GoogleClientSecrets,
    ): GoogleAuthorizationCodeFlow {
        return GoogleAuthorizationCodeFlow.Builder(
            this, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(tokenFolder))
            .setAccessType("offline")
            .build()
    }

    private tailrec fun Gmail.processMessages(
        user: String,
        label: Label,
        nextPageToken: String? = null,
        process: (Message) -> Unit,
    ) {

        val messages = users().messages().list(user).apply {
            labelIds = listOf(label.id)
            pageToken = nextPageToken
            includeSpamTrash = true
        }.execute()

        messages.messages.forEach { message ->
            process(message)
        }

        if (messages.nextPageToken != null) {
            processMessages(user, label, messages.nextPageToken, process)
        }
    }

    private fun String.parseAddress(): String {
        return if (contains("<")) {
            substringAfter("<").substringBefore(">")
        } else {
            this
        }
    }

    private fun Gmail.processFroms(
        user: String,
        label: Label,
        process: (String) -> Unit,
    ) {
        runBlocking(dispatcher) {
            processMessages(user, label) { m ->
                launch {
                    fun fetchAndProcess() {
                        try {
                            val message = users().messages().get(user, m.id).apply { format = "METADATA" }.execute()
                            message.payload.headers.find { it.name == "From" }?.let { from ->
                                process(from.value.parseAddress())
                            }
                        } catch (e: SocketTimeoutException) {
                            // Process eventual failures.
                            // Restart request on socket timeout.
                            e.printStackTrace()
                            fetchAndProcess()
                        } catch (e: Exception) {
                            // Process eventual failures.
                            e.printStackTrace()
                        }
                    }
                    fetchAndProcess()
                }
            }
        }
    }


    internal fun extract(labelName: String) {

        // Build a new authorized API client service.
        val httpTransport = GoogleNetHttpTransport
                .newTrustedTransport()
                .createRequestFactory { request ->
                    request.connectTimeout = TIMEOUT
                    request.readTimeout = TIMEOUT
                }.transport

        val service = Gmail.Builder(httpTransport, JSON_FACTORY, (httpTransport as NetHttpTransport).getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build()

        // Find the requested label
        val user = "me"
        val labelList = service.users().labels().list(user).execute()
        val label = labelList.labels
                .find { it.name == labelName } ?: error("Label `$labelName` is unknown.")

        // Process all From headers.
        val senders = mutableSetOf<String>()
        service.processFroms(user, label) {
            senders += it
        }
        senders.forEach(::println)
    }
}

class GmailUtilsStep {

    fun runGmailUtils(args: Array<String>) {

        if (!tokenFolder.exists()) {
            tokenFolder.mkdirs()
            log("Creating token folder...")
        }
        if (!appFolder.exists()) {
            appFolder.mkdirs()
            log("Creating app folder...")
        }
        if (args.size != 1) {
            println("Please specify exactly one parameter - the label/folder you want to extract emails from.")
            return
        }
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        GmailUtils().extract(args[0])
    }
}