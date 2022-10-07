@file:OptIn(ExperimentalFoundationApi::class)

package kt.nostr.nosky_compose.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kt.nostr.nosky_compose.direct_messages.ui.DiscussionScreen
import kt.nostr.nosky_compose.direct_messages.ui.Discussions
import kt.nostr.nosky_compose.home.ui.Home
import kt.nostr.nosky_compose.notifications.ui.NotificationsScreen
import kt.nostr.nosky_compose.reusable_components.PostView
import kt.nostr.nosky_compose.reusable_components.ProfileView
import kt.nostr.nosky_compose.settings.backend.AppThemeState
import kt.nostr.nosky_compose.settings.ui.SettingsScreen

@ExperimentalMaterialApi
@Composable
fun AppNavigation(navController: NavHostController,
                  appThemeState: AppThemeState, paddingConstraints: PaddingValues = PaddingValues.Absolute(), onThemeChange: (Boolean) -> Unit){
    NavHost(navController = navController, startDestination = NavigationItem.Home.route) {
        composable(NavigationItem.Home.route){

            Home(modifier = Modifier.padding(paddingConstraints), navigator = navController)
        }

        composable(route = "selected_post"){
            PostView { navController.navigateUp() }
        }


        composable(NavigationItem.Notifications.route){
            NotificationsScreen(navController)
        }

        composable(NavigationItem.Profile.route){
            ProfileView(isProfileMine = true, navController = navController, goBack = {})
        }

        composable(NavigationItem.Messages.route){
            Discussions(navController)
        }

        composable(route = "message"){
            DiscussionScreen(navController)
        }

        composable(NavigationItem.Settings.route){
            SettingsScreen(appThemeState, navController = navController, onThemeChange)
        }
    }
}



