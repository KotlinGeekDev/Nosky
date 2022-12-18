
package kt.nostr.nosky_compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.integrationpoint.NodeComponentActivity
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.activeElement
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.navigation.structure.NoskyRootNode
import kt.nostr.nosky_compose.navigation.structure.bottomNavTargets
import kt.nostr.nosky_compose.profile.LocalProfileDataStore
import kt.nostr.nosky_compose.settings.backend.AppThemeState
import kt.nostr.nosky_compose.settings.backend.SettingsDataStore
import kt.nostr.nosky_compose.settings.backend.ThemeStateSaver


class MainActivity : NodeComponentActivity() {

    private val profileDataProvider by lazy {
        LocalProfileDataStore(this.applicationContext)
    }

    private val themeSettings by lazy {
        SettingsDataStore(this.applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!profileDataProvider.containsIdentityData()){
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }

        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {

            val themePreference = themeSettings.getDarkThemeSetting()

            val appThemeState = rememberSaveable(saver = ThemeStateSaver) {
                AppThemeState(themePreference)
            }

            Screen(appIntegrationPoint = appyxIntegrationPoint,
                appThemeState,
                onThemeChange = { value ->
                appThemeState.switchTheme(value)
                themeSettings.updateDarkThemePreference(value)
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
                    currentDestination is Destination.ProfileInfo && item is Destination.ProfileInfo -> {
                        currentDestination.profile == null
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