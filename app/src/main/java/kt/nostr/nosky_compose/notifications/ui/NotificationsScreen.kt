package kt.nostr.nosky_compose.notifications.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.home.backend.opsList
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.reusable_ui_components.PostView
import kt.nostr.nosky_compose.reusable_ui_components.TopBar
import kt.nostr.nosky_compose.reusable_ui_components.theme.NoskycomposeTheme



@Composable
fun NotificationsScreen(
    navigator: BackStack<Destination> = BackStack(
        initialElement = Destination.Home,
        savedStateMap = null
    )
){

    BackHandler {
        navigator.run {
            elements.value.first().key.navTarget.let {
                singleTop(it)
            }
        }
    }

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
            BottomNavigationBar(backStackNavigator = navigator)
        }
    ) { paddingConstraints ->

        LazyColumn(Modifier.padding(paddingConstraints)){
            items(count = list.items.size, key = { index: Int -> index }){ index  ->
                PostView(
                    viewingPost = list.items[index],
                    isUserVerified = index.mod(2) != 0,
                    onPostClick = {
                        navigator.push(Destination.ViewingPost(clickedPost = it))
                    },
                    showProfile = {
                        navigator.push(Destination.Profile(isProfileSelected = true))
                    }
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

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
        NotificationsScreen()
    }
}