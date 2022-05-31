package ai.heart.classickbeats.shared.data

import ai.heart.classickbeats.shared.domain.ErrorJsonAdapter
import ai.heart.classickbeats.shared.network.SessionManager
import ai.heart.classickbeats.shared.result.Result
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import timber.log.Timber
import java.net.ConnectException
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.net.SocketTimeoutException
import java.net.UnknownHostException

const val DEFAULT_ERROR_MESSAGE = "Something went wrong. Please try again"

open class BaseRemoteDataSource(
    private val sessionManager: SessionManager
) {

    suspend fun <T : Any> safeApiCall(
        showNetworkError: Boolean = true,
        call: suspend () -> Response<T>
    ): Result<T> {
        try {
            val result = apiResponse(call)
            sessionManager.updateNetworkIssueStatus(true)
            return result
        } catch (e: Exception) {
            Timber.e(e)
            if (e is UnknownHostException || e is SocketTimeoutException || e is ConnectException) {
                if (showNetworkError) {
                    sessionManager.updateNetworkIssueStatus(false)
                }
                return Result.Error(e.message ?: DEFAULT_ERROR_MESSAGE)
            }
            return Result.Error(DEFAULT_ERROR_MESSAGE)
        }
    }

    private suspend fun <T : Any> apiResponse(
        call: suspend () -> Response<T>
    ): Result<T> {

        val response = call.invoke()

        return if (response.isSuccessful)
            Result.Success(response.body()!!)
        else {
            if (response.code() == HTTP_UNAUTHORIZED) { //when we get error code from response
                sessionManager.saveRefreshTokenStatus(false)
            }

            val errorJson = response.errorBody()?.string()

            val errorMessage = try {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val error = ErrorJsonAdapter(moshi).fromJson(errorJson)
                error?.errorList?.firstOrNull()
            } catch (e: Exception) {
                Timber.e(e)
                errorJson
            }
            Result.Error(errorMessage ?: DEFAULT_ERROR_MESSAGE)
        }
    }
}
