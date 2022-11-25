package kt.nostr.nosky_compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.profile.ProfileDataStore
import kt.nostr.nosky_compose.profile.ui.ProfileView

class ProfileViewNode(buildContext: BuildContext,
                      val isProfileSelected: Boolean = false,
                      //val isProfileMine: Boolean = !isProfileSelected,
                      private val navigator: BackStack<Destination>): Node(buildContext) {


    @Composable
    override fun View(modifier: Modifier) {
        if (!isProfileSelected){
            val context = LocalContext.current
            val profileStore = ProfileDataStore(context)

            ProfileView(
                profileSelected = isProfileSelected,
                user = profileStore.getProfile(),
                navController = navigator) {
                navigator.pop()
            }
        }
        else {
            ProfileView(
                profileSelected = isProfileSelected,
                navController = navigator) {
                navigator.pop()
            }
        }
    }


}