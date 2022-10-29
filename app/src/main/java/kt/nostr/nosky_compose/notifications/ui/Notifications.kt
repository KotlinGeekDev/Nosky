package kt.nostr.nosky_compose.notifications.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.reusable_components.Post
import kt.nostr.nosky_compose.reusable_components.TopBar
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme



@Composable
fun NotificationsScreen(navigator: NavController = rememberNavController()){

    val list by remember() {

        derivedStateOf {
            ListItems(opsList)
        }
    }

    Scaffold(
        topBar = {
            TopBar(tabTitle = "Notifications")
        },
        bottomBar = {
            BottomNavigationBar(navController = navigator)
        }
    ) { paddingConstraints ->
        //FeedProfileImage()

        LazyColumn(Modifier.padding(paddingConstraints)){
            items(count = list.items.size){ index  ->
                Post(userName = index.toString(), userPubkey = list.items[index],
                    isUserVerified = index.mod(2) != 0,
                    onPostClick = { navigator.navigate("selected_post")}, showProfile = {})
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

val opsList = List(20){ "Profile ${it + 1} "}

@Stable
class ListItems(val items: List<String>)


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true
)
@Composable
fun NotificationsPreview(){
    NoskycomposeTheme {
        NotificationsScreen(rememberNavController())
    }
}