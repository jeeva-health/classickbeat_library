package ai.heart.classickbeats.di

import ai.heart.classickbeats.data.db.AppDatabase
import ai.heart.classickbeats.data.reminder.ReminderApiService
import ai.heart.classickbeats.data.reminder.ReminderLocalDataSource
import ai.heart.classickbeats.data.reminder.ReminderRemoteDataSource
import ai.heart.classickbeats.data.reminder.cache.ReminderDao
import ai.heart.classickbeats.shared.network.SessionManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier

@InstallIn(SingletonComponent::class)
@Module
object ReminderDataModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalDataSource

    @Provides
    fun provideReminderApiService(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): ReminderApiService {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(ReminderApiService.ENDPOINT)
            .client(okHttpClient)
            .build()
            .create(ReminderApiService::class.java)
    }

    @Provides
    fun provideReminderDao(database: AppDatabase) = database.reminderDao()

    @Provides
    @RemoteDataSource
    fun provideReminderRemoteDataSource(
        reminderApiService: ReminderApiService,
        sessionManager: SessionManager
    ): ReminderRemoteDataSource {
        return ReminderRemoteDataSource(
            apiService = reminderApiService,
            sessionManager = sessionManager
        )
    }

    @Provides
    @LocalDataSource
    fun provideReminderLocalDataSource(
        reminderDao: ReminderDao
    ): ReminderLocalDataSource {
        return ReminderLocalDataSource(
            reminderDao = reminderDao
        )
    }
}
