package com.semilladigital.app.core.data.di


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Define el nombre de nuestro archivo de preferencias
private const val SESSION_PREFERENCES = "session_prefs"

// Extensión para crear el DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SESSION_PREFERENCES
)

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        // Hilt proveerá el DataStore usando el contexto de la app
        return context.dataStore
    }

    // Hilt ya sabe cómo crear 'SessionStorage' porque
    // le pusimos @Inject constructor y @Singleton
}