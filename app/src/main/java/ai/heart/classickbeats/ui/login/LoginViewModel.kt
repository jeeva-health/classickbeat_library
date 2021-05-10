package ai.heart.classickbeats.ui.login

import ai.heart.classickbeats.data.LoginRepository
import ai.heart.classickbeats.network.SessionManager
import ai.heart.classickbeats.shared.data.prefs.PreferenceStorage
import ai.heart.classickbeats.shared.result.Event
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val sessionManager: SessionManager,
    private val preferenceStorage: PreferenceStorage,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    enum class RequestType {
        LOGIN,
        REGISTER
    }

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    var currentFirebaseUser: FirebaseUser? = null

    var isUserRegistered: Boolean = false

    val showLoading = MutableLiveData<Boolean>(false)

    val firebaseAuthenticationState: LiveData<Event<AuthenticationState>> =
        Transformations.map(FirebaseUserLiveData()) { user ->
            currentFirebaseUser = user
            if (user != null) {
                Event(AuthenticationState.AUTHENTICATED)
            } else {
                Event(AuthenticationState.UNAUTHENTICATED)
            }
        }

    val loginState = MutableLiveData<Event<Boolean>>()

    val apiResponse = MutableLiveData<Event<RequestType>>()

    var apiError: String? = null

    fun logoutUser() {
        viewModelScope.launch {
            Firebase.auth.signOut()
            sessionManager.removeAuthToken()
            preferenceStorage.removeAllUserProps()
        }
    }

    fun isUserLoggedIn(): Boolean {
        return sessionManager.fetchAccessToken() != null
    }

    fun resetRefreshTokenStatus() {
        sessionManager.saveRefreshTokenStatus(true)
    }

    fun loginUser(firebaseToken: String) {
        showLoading.postValue(true)
        viewModelScope.launch {
            val (loginResponse, isUserRegistered) = loginRepository.loginUser(firebaseToken)
            this@LoginViewModel.isUserRegistered = isUserRegistered
            loginState.postValue(Event(loginResponse))
            showLoading.postValue(false)
        }
    }

    fun registerUser(fullName: String) {
        showLoading.postValue(true)
        viewModelScope.launch {
            val response = loginRepository.registerUser(fullName)
            apiResponse.postValue(Event(RequestType.REGISTER))
            showLoading.postValue(false)
        }
    }
}