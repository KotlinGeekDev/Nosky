package kt.nostr.nosky_compose

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import kt.nostr.nosky_compose.intro.NewProfileScreen
import kt.nostr.nosky_compose.intro.WelcomeScreen
import kt.nostr.nosky_compose.profile.model.ProfileViewModel
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.reusable_components.theme.Purple500
import kt.nostr.nosky_compose.settings.backend.AppThemeState
import kt.nostr.nosky_compose.settings.backend.ThemeStateSaver

class IntroActivity : ComponentActivity() {

    private val preferenceManager: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
    }

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            profileViewModel = viewModel(factory = ProfileViewModel.create())
            val privKey by profileViewModel.privKey.collectAsState()
            val pubKey by profileViewModel.pubKey.collectAsState()
            val darkMode = isSystemInDarkTheme()
            val appThemeState = rememberSaveable(saver = ThemeStateSaver) {
                AppThemeState(darkMode)
            }

            NoskycomposeTheme(darkTheme = appThemeState.isDark()) {
                IntroScreen(appThemeState = appThemeState,
                    inputPrivKey = privKey,
                    inputPubKey = pubKey,
                    onPrivKeyUpdate = profileViewModel::updatePrivKey,
                    onPubKeyUpdate = profileViewModel::updatePubKey,
                    onLoginClick = {
                    preferenceManager.edit {
                        putBoolean("profile_present", true)
                        putBoolean("dark_theme", appThemeState.isDark())
                    }
                    startActivity(
                        Intent(this@IntroActivity, MainActivity::class.java)
                            //.putExtra(PROFILE_DATA, Profile(pubKey, privKey))
                    )
                    finish()
                }, onThemeChange = {
                    appThemeState.switchTheme()
                })
            }
        }

    }

}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun IntroScreen(appThemeState: AppThemeState,
                inputPrivKey: String,
                inputPubKey: String,
                onPrivKeyUpdate: (String) -> Unit,
                onPubKeyUpdate: (String) -> Unit,
                onLoginClick:() -> Unit,
                onThemeChange:() -> Unit) {

    var userIsNew by rememberSaveable {
        mutableStateOf(false)
    }
    val themeIcon = remember {
        { if (appThemeState.isDark()) Icons.Default.LightMode else Icons.Default.DarkMode }
    }
    val backgroundColor: @Composable () -> Color = remember {
        { if (appThemeState.isDark()) MaterialTheme.colors.surface else Purple500 }
    }

    Scaffold(topBar = {
        TopAppBar(title = {}, actions = {
            Icon(imageVector = themeIcon(),
                contentDescription = "Switch App theme",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 5.dp, top = 3.dp)
                    .clickable { onThemeChange() }, tint = Color.White)
        },
            backgroundColor = backgroundColor(),
            elevation = 0.dp)
    }) { it ->
        AnimatedVisibility(visible = userIsNew,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()) {
            NewProfileScreen(themeState = appThemeState,
                onLoginClicked = { userIsNew = !userIsNew },
                onProfileCreated = {})
        }

        AnimatedVisibility(visible = !userIsNew, enter = fadeIn() + slideInHorizontally() + expandIn(),
            exit = fadeOut() + slideOutHorizontally()) {
            WelcomeScreen(appThemeState = appThemeState,
                privKey = inputPrivKey,
                updatePrivKey = onPrivKeyUpdate,
                pubKey = inputPubKey,
                updatePubKey = onPubKeyUpdate,
                onLoginClick = onLoginClick,
                onCreateProfileClick = { userIsNew = !userIsNew })
        }
    }


}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun IntroScreenPreview() {
    val darkTheme = rememberSaveable(saver = ThemeStateSaver) {
        AppThemeState(true)
    }
    var testPrivKey by remember {
        mutableStateOf("")
    }
    var testPubKey by remember {
        mutableStateOf("")
    }
    NoskycomposeTheme(darkTheme = darkTheme.isDark()) {
        IntroScreen (appThemeState = darkTheme,
            inputPrivKey = testPrivKey,
            inputPubKey = testPubKey,
            onPrivKeyUpdate = { testPrivKey = it },
            onPubKeyUpdate = { testPubKey = it },
            onLoginClick = { },
            onThemeChange = { darkTheme.switchTheme() })
    }
}