package kt.nostr.nosky_compose.settings.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alorma.settings.composables.SettingsMenuLink
import com.alorma.settings.composables.SettingsSwitch
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.BuildConfig
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.reusable_ui_components.TopBar
import kt.nostr.nosky_compose.reusable_ui_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.settings.backend.AppThemeState


@Composable
fun SettingsScreen(
    appThemeState: AppThemeState,
    navigator: BackStack<Destination> = BackStack(
        initialElement = Destination.Home,
        savedStateMap = null
    ),
    onStateChange: (Boolean) -> Unit
){

    BackHandler {
        navigator.run {
            elements.value.first().key.navTarget.let {
                singleTop(it)
            }
        }
    }

    var isOnMainPage by remember {
        mutableStateOf(false)
    }

    var showProfileManagement by remember {
        mutableStateOf(false)
    }



    AnimatedVisibility(
        visible = isOnMainPage,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AppInformationDetails(
            goBackToMainSettings = {
                isOnMainPage = !isOnMainPage
            }
        )
    }

    AnimatedVisibility(
        visible = !isOnMainPage,
//                && !showProfileManagement,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = { TopBar(tabTitle = "Settings") },
                bottomBar = { BottomNavigationBar(backStackNavigator = navigator) }
            ) { contentPadding ->
                Column(Modifier.padding(contentPadding)) {
                    ProfileSettingsView(
                        goToProfileSettingDetails = {
                            //showProfileManagement = !showProfileManagement
                        }
                    )
                    Divider(thickness = Dp.Hairline)
                    RelaySettingsView(goToRelayDetails = {})
                    DarkModeSettingView(appThemeState, onStateChange)
                    Description(
                        goToAppDetails = {
                            isOnMainPage = !isOnMainPage
                        }
                    )

                }
            }

            TextField(
                value = "App version : ${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE}",
                onValueChange = {},
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp, start = 40.dp),
                readOnly = true,
                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        textColor = Color.White.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
            )
        }
    }

}


@Composable
fun ProfileSettingsView(modifier: Modifier = Modifier, goToProfileSettingDetails: () -> Unit){
        SettingsMenuLink(
            icon = {
                Icon(imageVector = Icons.Rounded.Person, contentDescription = "Nostr profile")
            },
            title = { Text(text = "Profile Management") },
            subtitle = {
                Text(
                    text = "For showing the current keypair corresponding to the profile," +
                            " and creating a new profile"
                )
            },
            action = {
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = "Show details of your managed profiles",
                    modifier = Modifier.clickable { goToProfileSettingDetails() }
                )
            }
        ){ goToProfileSettingDetails() }

}

@Composable
fun RelaySettingsView(goToRelayDetails: () -> Unit) {
    SettingsMenuLink(
        icon = {
            Icon(
                imageVector = Icons.Filled.DeviceHub,
                contentDescription = "Manage the list of relays."
            )
        },
        title = { Text(text = "Relay Management") },
        subtitle = {
            Text(text = "To manage the relays the app connects to.")
        },
        action = {
            Icon(
                imageVector = Icons.Default.ArrowRight,
                contentDescription = "Go to list of relays."
            )
        }
    ) { goToRelayDetails() }
}

@Composable
fun DarkModeSettingView(appThemeState: AppThemeState, onStateChange:(Boolean) -> Unit) {
    
    val icon = if (appThemeState.isDark()) Icons.Default.DarkMode else Icons.Default.LightMode

    SettingsSwitch(icon = { 
        Icon(
        imageVector = icon,
        contentDescription = "Switch the app theme"
    ) },
        title = { Text(text = "Dark mode") },
        subtitle = { Text(text = "Sets the app's theme.") },
        checked = appThemeState.isDark(),
        onCheckedChange =  onStateChange
    )

}

@Composable
fun Description(goToAppDetails: () -> Unit){
    SettingsMenuLink(
        icon = { Icon(imageVector = Icons.Outlined.Android,"App Info Logo") },
        title = { Text("About Nosky") },
        subtitle = { Text("General information about the app.") },
        action = { IconButton(onClick = { goToAppDetails() }) {
            Icon(imageVector = Icons.Default.ArrowRight, contentDescription = "Show app details.")
            }
        }
    ){
        goToAppDetails()
    }


}


@Preview(showSystemUi = false, showBackground = true)
@Composable
fun ProfileSettingsPreview(){
    val appThemeState by remember {
        mutableStateOf(AppThemeState(true))
    }

    NoskycomposeTheme(appThemeState.isDark()) {
        SettingsScreen(appThemeState, navigator = BackStack(
            initialElement = Destination.Settings,
            savedStateMap = null)
        ) {
            appThemeState.switchTheme(it)
        }
    }
}