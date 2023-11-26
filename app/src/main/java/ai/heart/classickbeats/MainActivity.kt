package ai.heart.classickbeats

import ai.heart.classickbeats.databinding.ActivityMainBinding
//import ai.heart.classickbeats.model.Constants
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
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
import androidx.navigation.ui.onNavDestinationSelected
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null
        private set

    private lateinit var navController: NavController

//    @Inject
//    lateinit var updateManager: UpdateManager

//    @Inject
//    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Timber.e(throwable)
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

//        ViewCompat.setOnApplyWindowInsetsListener(binding?.rootLayout!!) { rootLayout: View, windowInsets: WindowInsetsCompat ->
//            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
//            rootLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
//                leftMargin = insets.left
//                topMargin = 0
//                rightMargin = insets.right
//                bottomMargin = insets.bottom
//            }
//            WindowInsetsCompat.CONSUMED
//        }

        navController = this.findNavController(R.id.nav_host_fragment)


//        createNotificationChannel()

//        updateManager.checkForImmediateUpdate()
    }

    override fun onResume() {
        super.onResume()

//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            binding?.apply {
//                when (destination.id) {
//                    R.id.scanFragment, R.id.historyFragment,
//                    R.id.loggingHomeFragment, R.id.wellnessHomeFragment, R.id.profileHomeFragment -> {
//                        showBottomNavigation()
//                    }
//                    else -> {
//                        hideBottomNavigation()
//                    }
//                }
//            }
//        }
    }

//    private fun createNotificationChannel() {
//        val name = getString(R.string.playback_channel_name)
//        val descriptionText = getString(R.string.playback_channel_description)
//        val importance = NotificationManager.IMPORTANCE_DEFAULT
//        val channel =
//            NotificationChannel(Constants.PLAYBACK_CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//        // Register the channel with the system
//        val notificationManager: NotificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UpdateManager.IMMEDIATE_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Timber.e("Update flow failed! Result code: $resultCode")
                // TODO(Start updated again)
            }
        }
    }*/
}
