package kt.nostr.nosky_compose.reusable_components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun AppTopBar(modifier: Modifier = Modifier,
              label: String,
              goBack: () -> Unit) {
    TopAppBar(title = { Text(text = label) },
        modifier = Modifier.then(modifier),
        navigationIcon = {
            IconButton(onClick = goBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
            }
        })

}

@Composable
fun AppTopBar(modifier: Modifier = Modifier,
              header: @Composable () -> Unit,
              menuActions: @Composable () -> Unit = {},
              goBack: () -> Unit) {
    TopAppBar(title = header,
        modifier = Modifier.then(modifier),
        navigationIcon = {
            IconButton(onClick = goBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
            }
        }, actions = {
            menuActions()
        })

}

@Composable
fun TopBar(tabTitle: String){
    TopAppBar(
        title = { Text(text = tabTitle, fontSize = 18.sp) },
        //backgroundColor = if (themeState.darkModeEnabled) MaterialTheme.colors.primary
        //else MaterialTheme.colors.primaryVariant,
        // contentColor = Color.White
    )
}