package ai.heart.classickbeats.di

import ai.heart.classickbeats.data.logging.LoggingApiService
import ai.heart.classickbeats.data.logging.LoggingRemoteDataSource
import ai.heart.classickbeats.shared.network.SessionManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@InstallIn(SingletonComponent::class)
@Module
object LoggingDataModule {

    @Provides
    fun provideLoggingApiService(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): LoggingApiService {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(LoggingApiService.ENDPOINT)
            .client(okHttpClient)
            .build()
            .create(LoggingApiService::class.java)
    }

    @Provides
    fun provideLoggingRemoteDataSource(
        loggingApiService: LoggingApiService,
        sessionManager: SessionManager
    ): LoggingRemoteDataSource {
        return LoggingRemoteDataSource(
            loggingApiService = loggingApiService,
            sessionManager = sessionManager
        )
    }
}
