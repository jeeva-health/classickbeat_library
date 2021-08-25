package ai.heart.classickbeats.di

import ai.heart.classickbeats.data.record.RecordApiService
import ai.heart.classickbeats.data.record.RecordRemoteDataSource
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
object RecordDataModule {

    @Provides
    fun provideRecordApiService(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): RecordApiService {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(RecordApiService.ENDPOINT)
            .client(okHttpClient)
            .build()
            .create(RecordApiService::class.java)
    }

    @Provides
    fun provideRecordRemoteDataSource(
        recordApiService: RecordApiService,
        sessionManager: SessionManager
    ): RecordRemoteDataSource {
        return RecordRemoteDataSource(
            recordApiService = recordApiService,
            sessionManager = sessionManager
        )
    }
}