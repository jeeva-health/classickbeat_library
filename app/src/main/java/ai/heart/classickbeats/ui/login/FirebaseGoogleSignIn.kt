package ai.heart.classickbeats.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import timber.log.Timber

class FirebaseGoogleSignIn :
    ActivityResultContract<GoogleSignInClient, GoogleSignInAccount?>() {

    override fun createIntent(context: Context, input: GoogleSignInClient?): Intent {
        return input?.signInIntent ?: throw Exception("GoogleSignInClient is null")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): GoogleSignInAccount? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        return try {
            task.getResult(ApiException::class.java)!!
        } catch (e: ApiException) {
            Timber.w("Google sign in failed $e")
            null
        }
    }
}