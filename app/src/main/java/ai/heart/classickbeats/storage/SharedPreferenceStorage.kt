package ai.heart.classickbeats.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreferenceStorage @Inject constructor(@ApplicationContext context: Context) :
    PreferenceStorage {

    companion object {
        const val PREF_NAME = "my_prefs"
        const val ON_BOARDING_COMPLETED = "on_boarding_completed"
        const val SELECTED_EXAM_ID = "selected_exam_id"
        const val USER_NAME = "user_name"
        const val USER_PHONE_NUMBER = "user_number"
        const val USER_EMAIL_ADDRESS = "user_email"
        const val USER_ID = "user_id"
    }

    private val prefs: Lazy<SharedPreferences> = lazy {
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).apply {
            registerOnSharedPreferenceChangeListener(changeListener)
        }
    }

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->

    }

    override var onBoardingCompleted: Boolean by BooleanPreference(
        prefs,
        ON_BOARDING_COMPLETED,
        false
    )

    override var userName: String by StringPreference(
        prefs,
        USER_NAME,
        "User"
    )

    override var userNumber: String by StringPreference(
        prefs,
        USER_PHONE_NUMBER,
        ""
    )

    override var userEmail: String by StringPreference(
        prefs,
        USER_EMAIL_ADDRESS,
        ""
    )

    override var userId: Long by LongPreference(
        prefs,
        USER_ID,
        1
    )

    fun removeAllUserProps() {
        val editor = prefs.value.edit()
        editor.remove(SELECTED_EXAM_ID)
        editor.remove(USER_NAME)
        editor.remove(USER_PHONE_NUMBER)
        editor.remove(USER_EMAIL_ADDRESS)
        editor.remove(USER_ID)
        editor.apply()
    }
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(name, value) }
    }
}

class LongPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Long
) : ReadWriteProperty<Any, Long> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.value.getLong(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.value.edit { putLong(name, value) }
    }
}

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String
) : ReadWriteProperty<Any, String> {

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return preferences.value.getString(name, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        preferences.value.edit { putString(name, value) }
    }
}