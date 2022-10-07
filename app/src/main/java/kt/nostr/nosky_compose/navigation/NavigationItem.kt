package kt.nostr.nosky_compose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String){
    object Home: NavigationItem("home", Icons.Filled.Home, "Home")
    object Profile: NavigationItem("profile", Icons.Filled.Person, "Profile")
    object Notifications: NavigationItem("notifications", Icons.Filled.Notifications, "Notifications")
    object Messages : NavigationItem("messages", Icons.Filled.Chat,"Messages")
    object Settings: NavigationItem("settings", Icons.Filled.Settings, "Settings")

}


