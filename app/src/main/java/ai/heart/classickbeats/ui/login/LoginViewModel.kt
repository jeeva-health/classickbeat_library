package ai.heart.classickbeats.ui.login

import ai.heart.classickbeats.data.user.UserRepository
import ai.heart.classickbeats.model.Gender
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.shared.data.login.LoginRepository
import ai.heart.classickbeats.shared.data.prefs.PreferenceStorage
import ai.heart.classickbeats.shared.domain.prefs.UserRegisteredActionUseCase
import ai.heart.classickbeats.shared.network.SessionManager
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
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
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val preferenceStorage: PreferenceStorage,
    private val userRegisteredActionUseCase: UserRegisteredActionUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    enum class RequestType {
        LOGIN,
        REGISTER
    }

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val genderList = Gender.values().toList()

    val genderListStr = genderList.map { it.displayStr }

    var selectedGender: Gender? = null

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
            userRegisteredActionUseCase.invoke(isUserRegistered)
            loginState.postValue(Event(loginResponse))
            showLoading.postValue(false)
        }
    }

    fun registerUser(user: User) {
        showLoading.postValue(true)
        viewModelScope.launch {
            val response = userRepository.registerUser(user)
            if (response.succeeded) {
                userRegisteredActionUseCase.invoke(true)
                apiResponse.postValue(Event(RequestType.REGISTER))
            } else {
                apiError = response.error
            }
            showLoading.postValue(false)
        }
    }
}
