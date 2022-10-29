package kt.nostr.nosky_compose.navigation.structure

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import kotlinx.parcelize.Parcelize
import kt.nostr.nosky_compose.direct_messages.ui.Discussions
import kt.nostr.nosky_compose.home.ui.Home
import kt.nostr.nosky_compose.notifications.ui.NotificationsScreen
import kt.nostr.nosky_compose.reusable_components.ProfileView
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
            Destination.Home -> node(buildContext){ Home() }
            Destination.Profile -> node(buildContext){
                ProfileView(
                    goBack = { navigateUp() }
                )
            }
            Destination.Notifications -> node(buildContext){ NotificationsScreen() }
            Destination.Messages -> node(buildContext){ Discussions() }
            Destination.Settings -> node(buildContext){
                SettingsScreen(appThemeState = themeState, onStateChange = onThemeChange)
            }
        }

    }


}

sealed class Destination : Parcelable {
    @Parcelize
    object Home : Destination()

    @Parcelize
    object Profile : Destination()

    @Parcelize
    object Notifications : Destination()

    @Parcelize
    object Messages : Destination()

    @Parcelize
    object Settings : Destination()
}