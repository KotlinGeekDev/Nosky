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
import com.bumble.appyx.navmodel.backstack.active
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.remove
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import kotlinx.parcelize.Parcelize
import kt.nostr.nosky_compose.direct_messages.ui.DiscussionScreen
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.navigation.DiscussionListViewNode
import kt.nostr.nosky_compose.navigation.FeedViewNode
import kt.nostr.nosky_compose.navigation.ProfileViewNode
import kt.nostr.nosky_compose.notifications.ui.NotificationsScreen
import kt.nostr.nosky_compose.profile.model.Profile
import kt.nostr.nosky_compose.common_components.ui.PostScreen
import kt.nostr.nosky_compose.settings.backend.AppThemeState
import kt.nostr.nosky_compose.settings.ui.SettingsScreen


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
        Children(navModel = backStack, transitionHandler = rememberBackstackFader())

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
                        //backStack.pop()
                          backStack.remove(backStack.active!!.key)


                    },
                    navigator = backStack)
            }
            is Destination.MyProfile -> ProfileViewNode(buildContext,
                profile = navTarget.profile,
                navigator = backStack)
            Destination.Notifications -> node(buildContext){
                NotificationsScreen(navigator = backStack)
            }
            is Destination.Messages -> DiscussionListViewNode(buildContext, navigator = backStack)
            Destination.Discussion -> node(buildContext){
                DiscussionScreen(navigator = backStack, goBack = { backStack.pop() })
            }
            Destination.Settings -> node(buildContext){
                SettingsScreen(appThemeState = themeState,
                    navigator = backStack,
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
    class MyProfile(val profile: Profile? = null) :
        Destination(icon = Icons.Filled.Person, title = "Profile")

    @Parcelize
    object Notifications : Destination(icon = Icons.Filled.Notifications, title = "Notifications")

    @Parcelize
    class Messages : Destination(icon = Icons.Filled.Chat, title = "Messages")

    @Parcelize
    object Discussion : Destination(title = "")

    @Parcelize
    object Settings : Destination(icon = Icons.Filled.Settings, title = "Settings")


}

fun bottomNavTargets(): List<Destination> {
    return listOf(
        Destination.Home,
        Destination.MyProfile(),
        Destination.Notifications,
        Destination.Messages(),
        Destination.Settings
    )
}
