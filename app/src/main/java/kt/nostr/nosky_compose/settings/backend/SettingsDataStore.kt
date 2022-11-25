package kt.nostr.nosky_compose.settings.backend

import android.app.UiModeManager
import android.content.Context
import androidx.core.content.edit

const val SETTINGS_DATA = "settings"
const val DARK_THEME = "dark_theme"

class SettingsDataStore(private val appContext: Context) {

    private val settingsPreferences = appContext.getSharedPreferences(SETTINGS_DATA, Context.MODE_PRIVATE)

    fun saveDarkThemePreference(darkMode: Boolean){
        settingsPreferences.edit {
            putBoolean(DARK_THEME, darkMode)
        }
    }

    fun updateDarkThemePreference(darkThemePref: Boolean){
        settingsPreferences.edit(commit = true) {
            if (settingsPreferences.contains(DARK_THEME))
                putBoolean(DARK_THEME, darkThemePref)
        }
    }

    fun getDarkThemeSetting(): Boolean {
        val uiModeProvider = appContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val systemDarkMode = uiModeProvider.nightMode == UiModeManager.MODE_NIGHT_YES
        val darkMode = settingsPreferences.getBoolean(DARK_THEME, systemDarkMode)

        return darkMode
    }


}