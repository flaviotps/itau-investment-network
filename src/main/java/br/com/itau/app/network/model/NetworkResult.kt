package br.com.itau.app.network.model

import retrofit2.HttpException
import retrofit2.Response


sealed interface NetworkResult<T : Any> {
    class Success<T : Any>(val data: T) : NetworkResult<T>
    class Error<T : Any>(val code: Int, val message: String?) : NetworkResult<T>
    class Exception<T : Any>(val exception: Throwable) : NetworkResult<T>
}


/**
 * Handles API responses and returns [NetworkResult].
 *
 * @param execute A suspend function that makes the API call.
 * @return [NetworkResult] representing the result of the API call.
 */
suspend fun <T : Any> handleApi(execute: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = execute()
        val body = response.body()
        when {
            response.isSuccessful && body != null -> NetworkResult.Success(body)
            else -> NetworkResult.Error(code = response.code(), message = response.message())
        }
    } catch (e: HttpException) {
        NetworkResult.Error(code = e.code(), message = e.message())
    } catch (e: Throwable) {
        NetworkResult.Exception(e)
    }
}

/**
 * Handles local data retrieval operations and returns [NetworkResult].
 *
 * @param execute A suspend function that retrieves local data.
 * @return [NetworkResult] representing the result of the local data retrieval.
 */
suspend fun <T : Any> handleLocal(execute: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(execute())
    } catch (e: Throwable) {
        NetworkResult.Exception(e)
    }
}
