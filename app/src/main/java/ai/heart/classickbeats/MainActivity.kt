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
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    private lateinit var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

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

        binding?.bottomNavigation?.setOnNavigationItemReselectedListener { menuItem ->
            menuItem.onNavDestinationSelected(navController)
        }

        createNotificationChannel()

        bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        }

        binding?.bottomSheet?.let {
            bottomSheetBehavior = BottomSheetBehavior.from(it)
            bottomSheetBehavior?.addBottomSheetCallback(bottomSheetCallback)
        }

        binding?.bottomNavigation?.setOnNavigationItemSelectedListener { item: MenuItem ->
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
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN


        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding?.apply {
                when (destination.id) {
                    R.id.scanFragment, R.id.historyFragment, R.id.loggingHomeFragment, R.id.wellnessHomeFragment, R.id.profileHomeFragment -> {
                        showBottomNavigation()
                    }
                    else -> {
                        hideBottomNavigation()
                    }
                }
            }
        }
    }

    fun hideSystemUI() {
        supportActionBar?.hide()
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

    fun showBottomNavigation() {
        binding?.bottomNavigation?.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        binding?.bottomNavigation?.visibility = View.GONE
    }

    fun navigateToHistoryFragment() {
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

    fun showBottomDialog(
        title: String,
        items: List<String>,
        itemClickListener: (Int) -> Unit = {},
        textSize: Float = 24.0f
    ) {
        val bottomSheetStringAdapter =
            BottomSheetStringAdapter(itemClickListener = itemClickListener)
        binding?.bottomSheetTitle?.text = title
        bottomSheetStringAdapter.updateTextSize(textSize)
        binding?.bottomSheetListItem?.adapter = bottomSheetStringAdapter
        bottomSheetStringAdapter.submitList(items)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun hideBottomDialog() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onDestroy() {
        bottomSheetBehavior?.removeBottomSheetCallback(bottomSheetCallback)
        super.onDestroy()
    }
}
