package ai.heart.classickbeats.di

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.data.remote.ApiService
import ai.heart.classickbeats.network.AccessTokenAuthenticator
import ai.heart.classickbeats.network.AuthInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

@InstallIn(ActivityRetainedComponent::class)
@Module
object NetworkModule {

    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        accessTokenAuthenticator: AccessTokenAuthenticator
    ): OkHttpClient {

        return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(accessTokenAuthenticator)
            .build()
    }

    @Provides
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }.apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }


    @ActivityRetainedScoped
    @Provides
    fun provideApiService(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): ApiService {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(ApiService.ENDPOINT)
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }
}