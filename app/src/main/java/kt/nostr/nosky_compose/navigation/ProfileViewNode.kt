package kt.nostr.nosky_compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.reusable_ui_components.ProfileView

class ProfileViewNode(buildContext: BuildContext,
                      val isProfileSelected: Boolean = false,
                      //val isProfileMine: Boolean = !isProfileSelected,
                      private val navigator: BackStack<Destination>): Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        ProfileView(profileSelected = isProfileSelected, navController = navigator) {
            navigator.pop()
        }
    }
}