package pt.tiagoduarte.challenge.listing.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import pt.tiagoduarte.challenge.data.remote.ProductApi
import pt.tiagoduarte.challenge.data.remote.RetrofitClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = RetrofitClient.json

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = RetrofitClient.okHttpClient

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = RetrofitClient.retrofit

    @Provides
    @Singleton
    fun provideProductApi(): ProductApi = RetrofitClient.productApi
}
