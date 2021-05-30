package ai.heart.classickbeats.di

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.shared.network.AccessTokenAuthenticator
import ai.heart.classickbeats.shared.network.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
}