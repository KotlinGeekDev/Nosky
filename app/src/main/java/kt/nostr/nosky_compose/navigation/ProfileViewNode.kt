package kt.nostr.nosky_compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.profile.LoggedInProfileProvider
import kt.nostr.nosky_compose.profile.model.LocalProfileViewModel
import kt.nostr.nosky_compose.profile.model.Profile
import kt.nostr.nosky_compose.profile.ui.ProfileView

class ProfileViewNode(buildContext: BuildContext,
                      val profile: Profile? = null,
                      private val navigator: BackStack<Destination>): Node(buildContext) {


    @Composable
    override fun View(modifier: Modifier) {

        val context = LocalContext.current
        val loggedInProfile = LoggedInProfileProvider.getLoggedProfile(context)
        val profileIsMine = (profile == null) || (profile.pubKey == loggedInProfile.pubKey)

        if (profileIsMine){
            val profileStore = viewModel<LocalProfileViewModel>(factory = LocalProfileViewModel.create())
            profileStore.updateProfile()
            if (profile != null && loggedInProfile.userName != profile.userName){
                profileStore.updateUserName(profile.userName)
                profileStore.saveProfile()
            }
            val localProfile by profileStore.newUserProfile.collectAsState()


            ProfileView(
                user = localProfile,
                navController = navigator) {
                navigator.pop()
            }
        }
        else {
            //val externalProfileProvider = viewModel<ExternalProfileViewModel>()
            if (profile != null) {
                ProfileView(
                    user = profile,
                    isProfileMine = profileIsMine,
                    navController = navigator) {
                    navigator.pop()
                }
            }
        }
    }


}