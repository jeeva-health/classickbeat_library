package ai.heart.classickbeats

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class UpdateManager @Inject constructor(@ActivityContext private val context: Context) {

    companion object {
        const val DAYS_FOR_FLEXIBLE_UPDATE = 5
        const val IMMEDIATE_UPDATE_REQUEST_CODE = 1010
    }

    private val appUpdateManager = AppUpdateManagerFactory.create(context)

    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    fun checkForImmediateUpdate() {
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                startImmediateDownload(appUpdateInfo)
            }
        }
    }

    fun checkForFlexibleUpdate() {
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {

            }
        }
    }

    fun isDownloadingUpdate(appUpdateInfo: AppUpdateInfo): Boolean {
        return appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                && appUpdateInfo.installStatus() == InstallStatus.DOWNLOADING
    }

    private fun startImmediateDownload(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            context as Activity,
            IMMEDIATE_UPDATE_REQUEST_CODE
        )
    }
}
