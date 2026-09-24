package pt.tiagoduarte.challenge.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pt.tiagoduarte.challenge.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val TIMEOUT_SECONDS = 15L

class RetrofitClient(private val baseUrl: String) {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor())
            .build()
    }

    @PublishedApi
    internal val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    inline fun <reified T> create(): T = retrofit.create(T::class.java)

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    companion object {
        val json: Json = Json { ignoreUnknownKeys = true }
    }
}
