package kt.nostr.nosky_compose.settings.backend

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver

@Stable
class AppThemeState(darkModeEnabled: Boolean) {
    private var mutableThemeState: MutableState<Boolean> = mutableStateOf(darkModeEnabled)


    val themeState = mutableThemeState

    fun isDark(): Boolean = mutableThemeState.value
    fun switchTheme(darkMode: Boolean) {
        mutableThemeState.value = darkMode
    }


}

val ThemeStateSaver = Saver<AppThemeState, Any>(
    save = { it.themeState.value },
    restore = { AppThemeState(it as Boolean) }
)