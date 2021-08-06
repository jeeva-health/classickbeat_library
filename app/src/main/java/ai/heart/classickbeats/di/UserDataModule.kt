package ai.heart.classickbeats.di

import ai.heart.classickbeats.data.user.remote.UserApiService
import ai.heart.classickbeats.data.user.remote.UserRemoteDataSource
import ai.heart.classickbeats.shared.network.SessionManager
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
object UserDataModule {

    @ActivityRetainedScoped
    @Provides
    fun provideUserApiService(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): UserApiService {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(UserApiService.ENDPOINT)
            .client(okHttpClient)
            .build()
            .create(UserApiService::class.java)
    }

    @ActivityRetainedScoped
    @Provides
    fun provideUserRemoteDataSource(
        userApiService: UserApiService,
        sessionManager: SessionManager
    ): UserRemoteDataSource {
        return UserRemoteDataSource(
            userApiService = userApiService,
            sessionManager = sessionManager
        )
    }
}