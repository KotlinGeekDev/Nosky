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
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.reusable_ui_components.PostView
import kt.nostr.nosky_compose.reusable_ui_components.TopBar
import kt.nostr.nosky_compose.reusable_ui_components.theme.NoskycomposeTheme



@Composable
fun NotificationsScreen(navigator: NavController = rememberNavController()){

    val list by remember() {

        derivedStateOf {
            PostsList(opsList)
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

        LazyColumn(Modifier.padding(paddingConstraints)){
            items(count = list.items.size, key = { index: Int -> index }){ index  ->
                PostView(
                    viewingPost = list.items[index],
                    isUserVerified = index.mod(2) != 0,
                    onPostClick = { navigator.navigate("selected_post")}, showProfile = {})
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

val opsList = listOf(
    Post(
        username = "Satoshi Nakamoto 1",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContext = "One of the user's very very long messages.",
        quotedPost = Post()
    ),
    Post(
        username = "Satoshi Nakamoto 2",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContext = "One of the user's very very long messages."
    ),
    Post(
        username = "Satoshi Nakamoto 3",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContext = "One of the user's very very long messages.",
        quotedPost = Post(
            username = "Satoshi Nakamoto 7",
            userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
            textContext = "One of the user's very very long messages."
        )
    ),
    Post(
        username = "Satoshi Nakamoto 4",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContext = "One of the user's very very long messages."
    ),
    Post(
        username = "Satoshi Nakamoto 5",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContext = "One of the user's very very long messages."
    ),
    Post(
        username = "Satoshi Nakamoto 6",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContext = "One of the user's very very long messages."
    ),
    Post(
        username = "Satoshi Nakamoto 7",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContext = "One of the user's very very long messages."
    ),
    Post(
        username = "Satoshi Nakamoto 8",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContext = "One of the user's very very long messages."
    ),
    Post(),
    Post(),
    Post(),
    Post(),
    Post(),
    Post(),
    Post()
)

@Stable
class PostsList(val items: List<Post>)


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true
)
@Preview(showSystemUi = true)
@Composable
fun NotificationsPreview(){
    NoskycomposeTheme {
        NotificationsScreen(rememberNavController())
    }
}