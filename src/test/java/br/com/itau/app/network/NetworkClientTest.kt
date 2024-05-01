package br.com.itau.app.network

import br.com.itau.app.network.model.NetworkResult
import br.com.itau.app.network.model.handleApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

data class SomeResponse(val test : String)

internal interface SomeRetrofitService {
    suspend fun someApiCall() : Response<SomeResponse>
}

class NetworkClientTest {

    private lateinit var networkClient: NetworkClient

    @Before
    fun setup() {
        networkClient = NetworkClient
    }

    @Test
    fun `test successful API call`() {
        runBlocking {
            val responseData = SomeResponse("Response data")
            val mockResponse = Response.success(responseData)

            val mockRetrofitService = mockk<SomeRetrofitService>()
            coEvery { mockRetrofitService.someApiCall() } returns mockResponse


            val result = handleApi { mockRetrofitService.someApiCall() }
            assert(result is NetworkResult.Success)
            assert((result as NetworkResult.Success).data == result.data)
        }
    }

    @Test
    fun `test API call failure`() {
        runBlocking {
            val errorBody = """{"error": "failed"}"""
            val responseData = SomeResponse(errorBody)
            val mockResponse  = mockk<Response<SomeResponse>>()
            val message = "Error"
            val code = 400
            every { mockResponse.message() } returns message
            every { mockResponse.body() } returns responseData
            every { mockResponse.isSuccessful } returns false
            every { mockResponse.code() } returns code

            val mockRetrofitService = mockk<SomeRetrofitService>()
            coEvery { mockRetrofitService.someApiCall() } returns mockResponse

            val result = handleApi { mockRetrofitService.someApiCall() }
            
            assert(result is NetworkResult.Error)
            assertEquals((result as NetworkResult.Error).message, message)
            assertEquals(result.code, code)
        }
    }

    @Test
    fun `test network exception`() {
        runBlocking {
            val message = "Network error"
            val mockException = IOException(message)

            val mockRetrofitService = mockk<SomeRetrofitService>()
            coEvery { mockRetrofitService.someApiCall() } throws mockException

            val result = handleApi { mockRetrofitService.someApiCall() }
            assert(result is NetworkResult.Exception)
            assert((result as NetworkResult.Exception).exception is IOException)
            assertEquals(result.exception.message, message)
        }
    }
}
