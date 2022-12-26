package kt.nostr.nosky_compose.settings.backend

import android.app.UiModeManager
import android.content.Context
import androidx.core.content.edit
import io.github.xxfast.kstore.*
import nostr.postr.Constants

const val SETTINGS_DATA = "settings"
const val DARK_THEME = "dark_theme"

const val RELAY_CONFIG_FILE = "relays.json"

class SettingsDataStore(appContext: Context) {



    private val defaultRelays = Constants.defaultRelays.map {
        NostrRelay(it.url, it.read, it.write)
    }

    private val settingsPreferences = appContext
        .getSharedPreferences(SETTINGS_DATA, Context.MODE_PRIVATE)
    private val uiModeProvider = appContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

    private val relayConfigFilePath = "${appContext.filesDir.path}/$RELAY_CONFIG_FILE"
    private val relayConfigStore: KStore<List<NostrRelay>> =
                            listStoreOf(relayConfigFilePath, defaultRelays)

    val relays = relayConfigStore.updatesOrEmpty

    suspend fun addRelay(newRelay: NostrRelay){
        relayConfigStore.plus(newRelay)
    }

    suspend fun removeRelay(relay: NostrRelay){
        relayConfigStore.minus(relay)
    }

    suspend fun resetToDefault(){
        relayConfigStore.reset()
    }



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

        val systemDarkMode = uiModeProvider.nightMode == UiModeManager.MODE_NIGHT_YES
        val darkMode = settingsPreferences.getBoolean(DARK_THEME, systemDarkMode)

        return darkMode
    }


}