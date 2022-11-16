package kt.nostr.nosky_compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.navmodel.backstack.BackStack
import kt.nostr.nosky_compose.home.ui.Home
import kt.nostr.nosky_compose.navigation.structure.Destination

class FeedViewNode(buildContext: BuildContext,
                   private val backStack: BackStack<Destination>,
                   //private val showPost: (Post) -> Unit
                    ) : Node(buildContext) {


    @Composable
    override fun View(modifier: Modifier) {
        Home(
//            showPost = showPost
        navigator = backStack
        )
    }

}