package ai.heart.classickbeats

import ai.heart.classickbeats.databinding.ActivityMainBinding
import ai.heart.classickbeats.model.Constants
import ai.heart.classickbeats.shared.data.login.LoginRepository
import ai.heart.classickbeats.shared.network.SessionManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    // It's needed to init loginRepoHolder class
    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(binding?.rootLayout!!) { rootLayout: View, windowInsets: WindowInsetsCompat ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            rootLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                topMargin = 0
                rightMargin = insets.right
                bottomMargin = insets.bottom
            }
            WindowInsetsCompat.CONSUMED
        }

        navController = this.findNavController(R.id.nav_host_fragment)
        appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.scanFragment,
                    R.id.historyFragment,
                    R.id.loggingHomeFragment,
                    R.id.wellnessHomeFragment,
                    R.id.profileHomeFragment
                )
            )
//        setupActionBarWithNavController(
//            navController,
//            appBarConfiguration
//        )

        binding?.bottomNavigation?.setOnItemSelectedListener { menuItem ->
            menuItem.onNavDestinationSelected(navController)
        }

        createNotificationChannel()

        binding?.bottomNavigation?.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.historyFragment -> {
                    navController.navigate(R.id.historyFragment)
                    true
                }
                R.id.loggingHomeFragment -> {
                    navController.navigate(R.id.loggingHomeFragment)
                    true
                }
                R.id.scanFragment -> {
                    navController.navigate(R.id.scanFragment)
                    true
                }
                R.id.wellnessHomeFragment -> {
                    navController.navigate(R.id.wellnessHomeFragment)
                    true
                }
                R.id.profileHomeFragment -> {
                    navController.navigate(R.id.profileHomeFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }

        // TODO: fix below code
//        sessionManager.updateNetworkIssueStatus(true)
//
//        loginViewModel.refreshTokenStatusLiveData.observe(this, { isTokenValid ->
//            if (!isTokenValid) {
//                loginViewModel.logoutUser()
//                Toast.makeText(this, "Session expired. Please login again", Toast.LENGTH_SHORT)
//                    .show()
//                GlobalScope.launch {
//                    delay(1000)
//                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
//                }
//            }
//        })
//        loginViewModel.resetRefreshTokenStatus()
    }

    override fun onResume() {
        super.onResume()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding?.apply {
                when (destination.id) {
                    R.id.scanFragment, R.id.historyFragment,
                    R.id.loggingHomeFragment, R.id.wellnessHomeFragment, R.id.profileHomeFragment -> {
                        showBottomNavigation()
                    }
                    else -> {
                        hideBottomNavigation()
                    }
                }
            }
        }
    }

    fun showSnackbar(message: String) {
        binding?.apply {
            Snackbar.make(bottomNavigation, message, LENGTH_SHORT)
                .apply { anchorView = bottomNavigation }.show()
        }
    }

    fun showBottomNavigation() {
        binding?.bottomNavigation?.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        binding?.bottomNavigation?.visibility = View.GONE
    }

    fun selectHistoryFragmentInBottomNavigation() {
        if (binding?.bottomNavigation?.selectedItemId != R.id.historyFragment) {
            binding?.bottomNavigation?.selectedItemId = R.id.historyFragment
        }
    }

    fun navigateToLoggingFragment() {
        if (binding?.bottomNavigation?.selectedItemId != R.id.loggingHomeFragment) {
            binding?.bottomNavigation?.selectedItemId = R.id.loggingHomeFragment
        }
    }

    fun navigateToHeartRateFragment() {
        if (binding?.bottomNavigation?.selectedItemId != R.id.scanFragment) {
            binding?.bottomNavigation?.selectedItemId = R.id.scanFragment
        }
    }

    fun navigateToWellnessFragment() {
        if (binding?.bottomNavigation?.selectedItemId != R.id.wellnessHomeFragment) {
            binding?.bottomNavigation?.selectedItemId = R.id.wellnessHomeFragment
        }
    }

    fun navigateToProfileFragment() {
        if (binding?.bottomNavigation?.selectedItemId != R.id.profileHomeFragment) {
            binding?.bottomNavigation?.selectedItemId = R.id.profileHomeFragment
        }
    }

    fun showLoadingBar() {
        Timber.i("showLoadingBar() called")
        binding?.progressBar?.visibility = View.VISIBLE
//        window?.setFlags(
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//        )
    }

    fun hideLoadingBar() {
        Timber.i("hideLoadingBar() called")
        binding?.progressBar?.visibility = View.GONE
        //window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.playback_channel_name)
            val descriptionText = getString(R.string.playback_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(Constants.PLAYBACK_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
