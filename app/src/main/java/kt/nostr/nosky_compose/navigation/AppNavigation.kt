@file:OptIn(ExperimentalFoundationApi::class)

package kt.nostr.nosky_compose.navigation

import androidx.compose.foundation.ExperimentalFoundationApi

//TODO: Remove this navigation infrastructure and replace it with Appyx-based navigation structure.

//@ExperimentalMaterialApi
//@Composable
//fun AppNavigation(navController: NavHostController,
//                  appThemeState: AppThemeState,
//                  paddingConstraints: PaddingValues = PaddingValues.Absolute(),
//                  onThemeChange: (Boolean) -> Unit){
//    NavHost(navController = navController, startDestination = NavigationItem.Home.route) {
//
//        composable(NavigationItem.Home.route){
//
//            Home(modifier = Modifier.padding(paddingConstraints), navigator = navController)
//        }
//
////        composable(route = "new_post"){
////            TestPopupScreen {
////                navController.navigateUp()
////            }
////        }
//
//        composable(route = "selected_post"){
//            PostScreen { navController.navigateUp() }
//        }
//
//
//        composable(NavigationItem.Notifications.route){
//            NotificationsScreen(navController)
//        }
//
//        composable(NavigationItem.Profile.route){
//            ProfileView(isProfileMine = true, navController = navController, goBack = {})
//        }
//
//        composable(NavigationItem.Messages.route){
//            Discussions(navController)
//        }
//
//        composable(route = "message"){
//            DiscussionScreen(navController)
//        }
//
//        composable(NavigationItem.Settings.route){
//            SettingsScreen(appThemeState, navController = navController, onThemeChange)
//        }
//    }
//}




