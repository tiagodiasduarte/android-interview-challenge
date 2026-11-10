package pt.tiagoduarte.challenge.rules

import androidx.annotation.VisibleForTesting
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import pt.tiagoduarte.challenge.data.remote.RetrofitClient
import pt.tiagoduarte.challenge.utils.readFromJSONToString
import java.net.HttpURLConnection

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class RemoteTestRule : TestWatcher() {

    @PublishedApi
    internal val mockWebServer = MockWebServer()

    override fun starting(description: Description) {
        super.starting(description)

        mockWebServer.start()
    }

    override fun finished(description: Description) {
        super.finished(description)

        mockWebServer.close()
    }

    fun mockWebServerResponse(body: String, code: Int) {
        mockWebServer.enqueue(MockResponse.Builder().body(body).code(code).build())
    }

    inline fun <reified Service> createTestService(): Service =
        RetrofitClient(baseUrl = mockWebServer.url("/").toString()).create()
}

fun RemoteTestRule.toServerSuccessResponse(jsonFile: String) {
    val body = readFromJSONToString(jsonFile)
    mockWebServerResponse(body, HttpURLConnection.HTTP_OK)
}

fun RemoteTestRule.toServerErrorResponse(jsonFile: String, code: Int = HttpURLConnection.HTTP_BAD_REQUEST) {
    val body = readFromJSONToString(jsonFile)
    mockWebServerResponse(body, code)
}
