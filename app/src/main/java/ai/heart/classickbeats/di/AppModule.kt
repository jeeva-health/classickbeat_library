package ai.heart.classickbeats.di

import ai.heart.classickbeats.shared.data.prefs.PreferenceStorage
import ai.heart.classickbeats.shared.data.prefs.SharedPreferenceStorage
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @ExperimentalCoroutinesApi
    @FlowPreview
    @Provides
    fun providePreferenceStorage(@ApplicationContext context: Context): PreferenceStorage =
        SharedPreferenceStorage(context)
}