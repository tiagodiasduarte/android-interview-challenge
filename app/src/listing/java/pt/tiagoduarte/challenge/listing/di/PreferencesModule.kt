package pt.tiagoduarte.challenge.listing.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pt.tiagoduarte.challenge.data.local.prefs.AppPreferences
import pt.tiagoduarte.challenge.data.local.prefs.AppPreferencesImpl
import pt.tiagoduarte.challenge.data.local.prefs.dataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences =
        AppPreferencesImpl(context.dataStore)
}
