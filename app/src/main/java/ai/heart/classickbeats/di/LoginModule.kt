package ai.heart.classickbeats.di

import ai.heart.classickbeats.data.remote.ApiService
import ai.heart.classickbeats.data.remote.LoginRemoteDataSource
import ai.heart.classickbeats.network.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@InstallIn(ActivityRetainedComponent::class)
@Module
object LoginModule {

    @ActivityRetainedScoped
    @Provides
    fun provideLoginRemoteDataSource(
        apiService: ApiService,
        sessionManager: SessionManager
    ): LoginRemoteDataSource {
        return LoginRemoteDataSource(apiService = apiService, sessionManager = sessionManager)
    }
}