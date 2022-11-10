package kt.nostr.nosky_compose.navigation.structure

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.navmodel.backstack.BackStack
import kt.nostr.nosky_compose.direct_messages.ui.Discussions

class DiscussionListViewNode(buildContext: BuildContext,
                             private val navigator: BackStack<Destination>) : Node(buildContext) {


    @Composable
    override fun View(modifier: Modifier) {
        Discussions(navController = navigator)
    }
}