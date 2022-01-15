package ai.heart.classickbeats.shared.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject


class AccessTokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val loginRepositoryHolder: LoginRepositoryHolder
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        val accessToken = sessionManager.fetchAccessToken()
        if (!isRequestWithAccessToken(response) || accessToken == null) {
            return null
        }

        synchronized(this) {
            // Check if the access token has been refreshed by any other thread
            val newAccessToken = sessionManager.fetchAccessToken()
            if (newAccessToken != accessToken && newAccessToken != null) {
                return newRequestWithAccessToken(response.request, newAccessToken)
            }

            val loginRepository = loginRepositoryHolder.loginRepository
            var isTokenRefreshed: Boolean
            runBlocking {
                isTokenRefreshed = loginRepository?.refreshToken() ?: false
            }

            val updatedAccessToken = sessionManager.fetchAccessToken()

            if (!isTokenRefreshed || updatedAccessToken == null) {
                return null
            }

            return newRequestWithAccessToken(response.request, updatedAccessToken)
        }
    }

    private fun isRequestWithAccessToken(response: Response): Boolean {
        val header = response.request.header("Authorization")
        return header != null && header.startsWith("Bearer")
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }
}