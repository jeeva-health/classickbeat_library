package ai.heart.classickbeats

import ai.heart.classickbeats.databinding.ActivityMainBinding
import ai.heart.classickbeats.network.SessionManager
import ai.heart.classickbeats.storage.SharedPreferenceStorage
import ai.heart.classickbeats.ui.login.LoginViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    @Inject
    lateinit var sharedPreferenceStorage: SharedPreferenceStorage

    @Inject
    lateinit var sessionManager: SessionManager

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        sessionManager.updateNetworkIssueStatus(true)

        loginViewModel.refreshTokenStatusLiveData.observe(this, { isTokenValid ->
            if (!isTokenValid) {
                loginViewModel.logoutUser()
                Toast.makeText(this, "Session expired. Please login again", Toast.LENGTH_SHORT)
                    .show()
                GlobalScope.launch {
                    delay(1000)
                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                }
            }
        })
        loginViewModel.resetRefreshTokenStatus()
    }

    override fun onResume() {
        super.onResume()
    }

    fun hideSystemUI() {
        supportActionBar?.hide()
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun setPageTitle() {

    }

    fun showLoadingBar() {
        Timber.i("showLoadingBar() called")
        binding?.progressBar?.visibility = View.VISIBLE
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    fun hideLoadingBar() {
        Timber.i("hideLoadingBar() called")
        binding?.progressBar?.visibility = View.GONE
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}