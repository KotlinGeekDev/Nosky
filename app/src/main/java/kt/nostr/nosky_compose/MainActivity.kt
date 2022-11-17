@file:OptIn(ExperimentalMaterialApi::class)

package kt.nostr.nosky_compose

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.integrationpoint.NodeComponentActivity
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.activeElement
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.navigation.structure.NoskyRootNode
import kt.nostr.nosky_compose.navigation.structure.bottomNavTargets
import kt.nostr.nosky_compose.reusable_ui_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.settings.backend.AppThemeState
import kt.nostr.nosky_compose.settings.backend.ThemeStateSaver

const val PROFILE_DATA = "profile_data"

class MainActivity : NodeComponentActivity() {
    private val preferenceManager: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!preferenceManager.contains("profile_present")
            || !preferenceManager.getBoolean("profile_present", false)){
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }

        //val obtainedProfile = intent.getParcelableExtra<Profile>(PROFILE_DATA)

        setContent {

            val themePreference = preferenceManager.getBoolean("dark_theme", isSystemInDarkTheme())
//            val themePreference = rememberPreferenceBooleanSettingState(
//                key = "dark_theme",
//                defaultValue = isSystemInDarkTheme()
//            )
            val appThemeState = rememberSaveable(saver = ThemeStateSaver) {
                AppThemeState(themePreference)
            }

            Screen(appIntegrationPoint = appyxIntegrationPoint,
                appThemeState,
                onThemeChange = { value ->
                appThemeState.switchTheme(value)
                this.preferenceManager.edit(commit = true) {
                    putBoolean("dark_theme", appThemeState.isDark())
                }
                //themePreference.value = appThemeState.isDark()
            })
        }

    }


}



@Composable
fun Screen(appIntegrationPoint: IntegrationPoint,
           theme: AppThemeState, onThemeChange: (Boolean) -> Unit){

    NoskycomposeTheme(theme.themeState.value) {

        Surface {
            NodeHost(integrationPoint = appIntegrationPoint){
                NoskyRootNode(rootBuildContext = it,
                    themeState = theme, onThemeChange = onThemeChange)
            }


        }
    }


}


@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier,
                        backStackNavigator: BackStack<Destination>,
                        isNewNotification: Boolean = false){


    val navItems = remember {

        bottomNavTargets()
    }

    BottomNavigation(modifier = modifier) {

        val currentDestination = backStackNavigator.activeElement


        navItems.forEach { item ->
            BottomNavigationItem(
                icon = {
                     BadgedBox(badge = {
                         this@BottomNavigation.AnimatedVisibility(
                             visible = item == Destination.Notifications
                                     && isNewNotification,
                             enter = fadeIn(), exit = fadeOut()){
                             Badge()
                         }
                     }) {
                         if (item.icon != null){
                             Icon(item.icon, contentDescription = item.title)
                         }
                     }
                },
                //label = { Text(text = item.title, overflow = TextOverflow.Ellipsis, maxLines = 1) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                selected = when {
                    currentDestination is Destination.Profile && item is Destination.Profile -> {
                        !currentDestination.isProfileSelected
                    }
                    else -> {
                        currentDestination?.title == item.title
                    }
                },
                alwaysShowLabel = false,
                onClick = {
                    backStackNavigator.singleTop(item)

                }
            )

        }

    }

}



@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    NoskycomposeTheme {
        Screen(appIntegrationPoint = IntegrationPointStub(),
            AppThemeState(darkModeEnabled = true), { })
    }
}