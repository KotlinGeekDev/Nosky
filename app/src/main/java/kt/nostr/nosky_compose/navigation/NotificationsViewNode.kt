package kt.nostr.nosky_compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.navmodel.backstack.BackStack
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.notifications.ui.NotificationsScreen

class NotificationsViewNode(buildContext: BuildContext,
                            private val backStack: BackStack<Destination>
                            ): Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier){

        NotificationsScreen(navigator = backStack)

    }

}