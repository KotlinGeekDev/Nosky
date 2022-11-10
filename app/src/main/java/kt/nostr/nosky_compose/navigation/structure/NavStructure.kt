package kt.nostr.nosky_compose.navigation.structure

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import kotlinx.parcelize.Parcelize
import kt.nostr.nosky_compose.direct_messages.ui.DiscussionScreen
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.notifications.ui.NotificationsScreen
import kt.nostr.nosky_compose.reusable_ui_components.PostScreen
import kt.nostr.nosky_compose.settings.backend.AppThemeState
import kt.nostr.nosky_compose.settings.ui.SettingsScreen

//TODO: Complete this infrastructure and replace the current navigation infrastructure with it.

class NoskyRootNode(
    rootBuildContext: BuildContext,
    private val themeState: AppThemeState,
    private val onThemeChange: (Boolean) -> Unit,
    private val backStack: BackStack<Destination> = BackStack(
        initialElement = Destination.Home,
        savedStateMap = rootBuildContext.savedStateMap
    )
) : ParentNode<Destination>(
    navModel = backStack,
    buildContext = rootBuildContext
) {


    @Composable
    override fun View(modifier: Modifier){
        Children(navModel = backStack)

    }

    override fun resolve(navTarget: Destination, buildContext: BuildContext): Node {


        return when(navTarget){
            Destination.Home -> FeedViewNode(buildContext, backStack,
//                showPost = { post ->
//                    backStack.push(Destination.ViewingPost(clickedPost = post))
//                }
            )

            is Destination.ViewingPost -> node(buildContext){
                PostScreen(
                    currentPost = navTarget.clickedPost,
                    goBack = {
                        navigateUp()
                    },
                    navigator = backStack)
            }
            is Destination.Profile -> ProfileViewNode(buildContext,
                isProfileSelected = navTarget.isProfileSelected, navigator = backStack)
            Destination.Notifications -> node(buildContext){ NotificationsScreen() }
            Destination.Messages -> DiscussionListViewNode(buildContext, navigator = backStack)
            Destination.Discussion -> node(buildContext){
                DiscussionScreen(navigator = backStack, goBack = { navigateUp() })
            }
            Destination.Settings -> node(buildContext){
                SettingsScreen(appThemeState = themeState,
                    navController = backStack,
                    onStateChange = onThemeChange)
            }
        }

    }


}

sealed class Destination(val icon: ImageVector? = null, val title: String) : Parcelable {
    @Parcelize
    object Home : Destination(icon = Icons.Filled.Home, title = "Home")

    @Parcelize
    class ViewingPost(val clickedPost: Post = Post()) : Destination(title = "")

    @Parcelize
    class Profile(val isProfileSelected: Boolean = false) :
        Destination(icon = Icons.Filled.Person, title = "Profile")

    @Parcelize
    object Notifications : Destination(icon = Icons.Filled.Notifications, title = "Notifications")

    @Parcelize
    object Messages : Destination(icon = Icons.Filled.Chat, title = "Messages")

    @Parcelize
    object Discussion : Destination(title = "")

    @Parcelize
    object Settings : Destination(icon = Icons.Filled.Settings, title = "Settings")


}

fun bottomNavDestinations(): Array<Destination> {
    return arrayOf(
        Destination.Home,
        Destination.Profile(),
        Destination.Notifications,
        Destination.Messages,
        Destination.Settings
    )
}
