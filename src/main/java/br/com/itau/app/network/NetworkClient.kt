package br.com.itau.app.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


private const val HOST_URL = "https://itau-investiments.wiremockapi.cloud/"

/**
 * Singleton object responsible for creating and configuring Retrofit client instances.
 */
object NetworkClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val gson: Gson = GsonBuilder()
        .create()

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(HOST_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()


    /**
     * Creates an instance of the specified Retrofit service interface.
     * @param service The class representing the Retrofit service interface.
     * @return An instance of the specified service interface.
     */
    inline fun <reified T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    /**
     * Invokes the 'create' function to create an instance of the specified Retrofit service interface.
     * @param service The class representing the Retrofit service interface.
     * @return An instance of the specified service interface.
     */
    inline operator fun <reified T> invoke(service: Class<T>) = create(service)
}