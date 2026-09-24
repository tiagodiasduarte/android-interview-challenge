package pt.tiagoduarte.challenge.data.remote.api

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pt.tiagoduarte.challenge.rules.RemoteTestRule
import pt.tiagoduarte.challenge.rules.toServerErrorResponse
import pt.tiagoduarte.challenge.rules.toServerSuccessResponse
import pt.tiagoduarte.challenge.stubs.GET_PRODUCTS_ERROR_RESPONSE
import pt.tiagoduarte.challenge.stubs.GET_PRODUCTS_SUCCESS_RESPONSE
import pt.tiagoduarte.challenge.stubs.productListResponseStub
import retrofit2.HttpException
import java.net.HttpURLConnection

class ProductApiTest {

    @get:Rule
    val remoteRule = RemoteTestRule()

    private val api: ProductApi by lazy { remoteRule.createTestService() }

    @Test
    fun `given a success response when getProducts is called then returns the parsed products`() = runTest {
        // Given
        remoteRule.toServerSuccessResponse(GET_PRODUCTS_SUCCESS_RESPONSE)

        // When
        val response = api.getProducts()

        // Then
        assertEquals(productListResponseStub(), response)
    }

    @Test
    fun `given an error response when getProducts is called then throws HttpException with the error code`() =
        runTest {
            // Given
            val errorCode = HttpURLConnection.HTTP_INTERNAL_ERROR
            remoteRule.toServerErrorResponse(GET_PRODUCTS_ERROR_RESPONSE, errorCode)

            // When
            val exception = runCatching { api.getProducts() }.exceptionOrNull()

            // Then
            assertTrue(exception is HttpException)
            assertEquals(errorCode, (exception as HttpException).code())
        }
}
