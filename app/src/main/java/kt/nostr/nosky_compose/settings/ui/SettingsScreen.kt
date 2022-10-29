package kt.nostr.nosky_compose.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alorma.settings.composables.SettingsMenuLink
import com.alorma.settings.composables.SettingsSwitch
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Bitcoin
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.reusable_components.TopBar
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.settings.backend.AppThemeState


@Composable
fun SettingsScreen(
    appThemeState: AppThemeState,
    navController: NavController = rememberNavController(),
    onStateChange: (Boolean) -> Unit){


        Scaffold(
            topBar = { TopBar(tabTitle = "Settings") },
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) {  contentPadding ->
            Column(Modifier.padding(contentPadding)) {
                ProfileSettingsView()
                Divider(thickness = Dp.Hairline)
                DarkModeSettingView(appThemeState, onStateChange)
                Description()
            }
        }

}


@Composable
fun ProfileSettingsView(modifier: Modifier = Modifier){
        SettingsMenuLink(
            icon = { Icon(imageVector = Icons.Rounded.Person, contentDescription = "Nostr profile") },
            title = { Text(text = "Profile Management") },
            subtitle = {
                Text(text = "For showing the current keypair corresponding to the profile, and creating a new profile")
            },
            action = {
                Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = "Click")
            }
        ){ }

}

@Composable
fun DarkModeSettingView(appThemeState: AppThemeState, onStateChange:(Boolean) -> Unit) {


    SettingsSwitch(icon = { Icon(
        imageVector = if (appThemeState.isDark()) Icons.Default.DarkMode else Icons.Default.LightMode,
        contentDescription = "Switch the app theme"
    ) },
        title = { Text(text = "Dark mode") },
        subtitle = { Text(text = "Sets the app's theme.") },
        checked = appThemeState.isDark(),
        onCheckedChange =  onStateChange
    )

}

@Composable
fun Description(){
    var showDetails by remember {
        mutableStateOf(false)
    }
    SettingsMenuLink(
        icon = { Icon(imageVector = Icons.Outlined.Android,"App Info Logo") },
        title = { Text("About Nosky") },
        subtitle = { Text("General information about the app.") },
        action = { IconButton(onClick = { showDetails = !showDetails }) {
            val iconImage = if (showDetails) Icons.Default.ArrowDropDown else Icons.Default.ArrowRight
            val description = if (showDetails) "Show the app's info" else "Close the app's info"

            Icon(imageVector = iconImage, contentDescription = description)
            }
        }
    ){}

    if (showDetails) {
        DescriptionDetails()
    } else {
        Column {
            TextField(value = "Nothing to see here.", onValueChange = {}, readOnly = true)
        }
    }

}

@Composable
fun DescriptionDetails() {

        Column {
            SmallElement(image = Icons.Rounded.AccountTree, description = "Test")
            SmallElement(image = FontAwesomeIcons.Brands.Bitcoin, description = "Test_2")
        }

}

@Composable
fun SmallElement(image: ImageVector, description: String) {
    Icon(imageVector = image, contentDescription = description, Modifier.height(25.dp))
}


@Preview(showSystemUi = false, showBackground = true)
@Composable
fun ProfileSettingsPreview(){
    val appThemeState by remember {
        mutableStateOf(AppThemeState(true))
    }

    NoskycomposeTheme(appThemeState.isDark()) {
        SettingsScreen(appThemeState, rememberNavController()) { appThemeState.switchTheme() }
    }
}