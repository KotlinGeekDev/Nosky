package kt.nostr.nosky_compose.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kt.nostr.nosky_compose.settings.backend.NostrRelay
import kt.nostr.nosky_compose.settings.backend.SettingsDataStore
import kt.nostr.nosky_compose.settings.backend.relayList

class SettingsViewModel(context: Context): ViewModel() {

    private val settingsDataStore = SettingsDataStore(context.applicationContext)

    val relays = settingsDataStore.relays
        .stateIn(viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            relayList
        )

    fun addRelay(newRelay: NostrRelay) {
        viewModelScope.launch {
            settingsDataStore.addRelay(newRelay)
        }
    }

    fun removeRelay(relay: NostrRelay){
        viewModelScope.launch {
            settingsDataStore.removeRelay(relay)
        }
    }

    fun reset() {
        viewModelScope.launch {
            settingsDataStore.resetToDefault()
        }
    }

}