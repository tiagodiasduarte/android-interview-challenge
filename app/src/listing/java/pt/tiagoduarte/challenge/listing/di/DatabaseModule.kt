package pt.tiagoduarte.challenge.listing.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pt.tiagoduarte.challenge.data.local.db.AppDatabase
import pt.tiagoduarte.challenge.data.local.db.ProductDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()
}
