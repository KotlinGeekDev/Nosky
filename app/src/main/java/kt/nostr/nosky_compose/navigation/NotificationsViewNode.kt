package kt.nostr.nosky_compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.navmodel.backstack.BackStack
import kotlinx.coroutines.cancel
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.notifications.NotificationsViewModel
import kt.nostr.nosky_compose.notifications.ui.NotificationsScreen

class NotificationsViewNode(buildContext: BuildContext,
                            private val backStack: BackStack<Destination>
                            ): Node(buildContext) {


    @Composable
    override fun View(modifier: Modifier){

        val notificationsViewModel = viewModel<NotificationsViewModel>()
        val uiState by notificationsViewModel.uiState.collectAsState()
        DisposableEffect(key1 = uiState){
            notificationsViewModel.fetchNotifications()
            onDispose { notificationsViewModel.viewModelScope.cancel() }
        }

        NotificationsScreen(navigator = backStack, notificationsUiState = uiState)

    }

}