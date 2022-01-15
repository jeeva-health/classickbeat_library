package ai.heart.classickbeats.di

import ai.heart.classickbeats.data.meditation.MeditationApiService
import ai.heart.classickbeats.data.meditation.MeditationRemoteDataSource
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
object MeditationDataModule {

    @Provides
    fun provideMeditationApiService(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): MeditationApiService {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(MeditationApiService.ENDPOINT)
            .client(okHttpClient)
            .build()
            .create(MeditationApiService::class.java)
    }

    @Provides
    fun provideMeditationRemoteDataSource(
        apiService: MeditationApiService,
        sessionManager: SessionManager
    ): MeditationRemoteDataSource {
        return MeditationRemoteDataSource(
            apiService = apiService,
            sessionManager = sessionManager
        )
    }
}
