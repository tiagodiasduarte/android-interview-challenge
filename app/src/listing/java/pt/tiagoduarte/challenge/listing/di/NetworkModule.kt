package pt.tiagoduarte.challenge.listing.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pt.tiagoduarte.challenge.BuildConfig
import pt.tiagoduarte.challenge.data.remote.RetrofitClient
import pt.tiagoduarte.challenge.data.remote.api.ProductApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitClient(): RetrofitClient = RetrofitClient(BuildConfig.BASE_URL)

    @Provides
    @Singleton
    fun provideProductApi(retrofitClient: RetrofitClient): ProductApi = retrofitClient.create()
}
