package kt.nostr.nosky_compose.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.home.backend.opsList

sealed class NotificationsUiState() {
    object Loading: NotificationsUiState()
    object Empty: NotificationsUiState()
    class Success(val listOfPosts: List<Post>): NotificationsUiState()
    class Error(val e: Exception): NotificationsUiState()
}

class NotificationsViewModel(): ViewModel(){

    private val _uiState: MutableStateFlow<NotificationsUiState> =
        MutableStateFlow(NotificationsUiState.Empty)
    val uiState = _uiState.asStateFlow()

    private val notificationsCache: MutableList<Post> = mutableListOf()

    fun fetchNotifications() {
        _uiState.value = NotificationsUiState.Loading
        viewModelScope.launch {
            delay(3000)
            notificationsCache.addAll(opsList)
            if (notificationsCache.isEmpty()){
                _uiState.update { NotificationsUiState.Empty }
            } else {
                _uiState.update { NotificationsUiState.Success(opsList) }
            }

        }
    }

}