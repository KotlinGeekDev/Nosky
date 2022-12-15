@file:OptIn(ExperimentalAnimationApi::class)

package kt.nostr.nosky_compose.notifications.ui

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.common_components.ui.DotsFlashing
import kt.nostr.nosky_compose.common_components.ui.PostView
import kt.nostr.nosky_compose.common_components.ui.TopBar
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.notifications.NotificationsUiState
import kt.nostr.nosky_compose.profile.model.Profile



@Composable
fun NotificationsScreen(
    navigator: BackStack<Destination> = BackStack(
        initialElement = Destination.Home,
        savedStateMap = null
    ),
    notificationsUiState: NotificationsUiState = NotificationsUiState.Empty
){

    BackHandler {
        navigator.run {
            elements.value.first().key.navTarget.let {
                singleTop(it)
            }
        }
    }

//    val list by remember() {
//
//        derivedStateOf {
//            PostList(opsList)
//        }
//    }

    Scaffold(
        topBar = {
            TopBar(tabTitle = "Notifications")
        },
        bottomBar = {
            BottomNavigationBar(backStackNavigator = navigator)
        }
    ) { paddingConstraints ->

        Crossfade(
            targetState = notificationsUiState,
            modifier = Modifier.padding(paddingConstraints)
        ) { uiState ->
            when(uiState){
                NotificationsUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No notifications.")
                    }
                }
                is NotificationsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        //CircularProgressIndicator(Modifier.align(Alignment.Center))
                        Row(modifier = Modifier.align(Alignment.Center)) {
                            Text(text = "Error loading notifications: ${uiState.e.message}")

                        }
                    }
                    Log.e("NoskyApp", uiState.e.message, uiState.e)
                }
                NotificationsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        //CircularProgressIndicator(Modifier.align(Alignment.Center))
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            DotsFlashing(
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
                is NotificationsUiState.Success -> {
                    LazyColumn(){
                        itemsIndexed(items = uiState.listOfPosts,
                            key = { index: Int, post: Post -> index }
                        ){ index, post  ->
                            PostView(
                                viewingPost = post,
                                isUserVerified = index.mod(2) != 0,
                                onPostClick = {
                                    navigator.push(Destination.ViewingPost(clickedPost = it))
                                },
                                showProfile = {
                                    navigator.push(
                                        Destination.MyProfile(
                                            Profile(
                                                pubKey = post.userKey,
                                                userName = post.username
                                            )
                                        )
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
            }
        }

    }
}


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