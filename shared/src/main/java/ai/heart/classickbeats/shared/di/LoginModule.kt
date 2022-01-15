package ai.heart.classickbeats.shared.di

import ai.heart.classickbeats.shared.data.login.LoginApiService
import ai.heart.classickbeats.shared.data.login.LoginRemoteDataSource
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
object LoginModule {

    @ActivityRetainedScoped
    @Provides
    fun provideLoginApiService(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): LoginApiService {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(LoginApiService.ENDPOINT)
            .client(okHttpClient)
            .build()
            .create(LoginApiService::class.java)
    }

    @ActivityRetainedScoped
    @Provides
    fun provideLoginRemoteDataSource(
        loginApiService: LoginApiService,
        sessionManager: SessionManager
    ): LoginRemoteDataSource {
        return LoginRemoteDataSource(loginApiService = loginApiService, sessionManager = sessionManager)
    }
}