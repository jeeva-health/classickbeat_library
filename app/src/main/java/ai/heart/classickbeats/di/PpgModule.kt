package ai.heart.classickbeats.di

import ai.heart.classickbeats.data.ppg.PpgApiService
import ai.heart.classickbeats.data.ppg.PpgRemoteDataSource
import ai.heart.classickbeats.network.SessionManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@InstallIn(ActivityRetainedComponent::class)
@Module
object PpgModule {

    @ActivityRetainedScoped
    @Provides
    fun providePpgApiService(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): PpgApiService {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(PpgApiService.ENDPOINT)
            .client(okHttpClient)
            .build()
            .create(PpgApiService::class.java)
    }

    @ActivityRetainedScoped
    @Provides
    fun providePpgRemoteDataSource(
        ppgApiService: PpgApiService,
        sessionManager: SessionManager
    ): PpgRemoteDataSource {
        return PpgRemoteDataSource(ppgApiService = ppgApiService, sessionManager = sessionManager)
    }
}