package kt.nostr.nosky_compose.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.home.backend.FeedViewModel
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.home.backend.opsList
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.notifications.ui.PostsList
import kt.nostr.nosky_compose.reusable_ui_components.DotsFlashing
import kt.nostr.nosky_compose.reusable_ui_components.PostView
import kt.nostr.nosky_compose.reusable_ui_components.theme.NoskycomposeTheme

//TODO: Replace double AnimatedVisibility below with single AnimatedContent.

@Composable
fun Home(modifier: Modifier = Modifier,
         //showPost: (Post) -> Unit,
         navigator: BackStack<Destination> = BackStack(
             initialElement = Destination.Home,
             savedStateMap = null
         )
) {

    val feedViewModel: FeedViewModel = viewModel()
    val feed by feedViewModel.feedContent.collectAsState()

    DisposableEffect(key1 = feed){
        feedViewModel.getUpdateFeed()
        onDispose {  }
    }

    val scaffoldState = rememberScaffoldState()

    HomeView(
        homeFeed = feed,
        scaffoldState = scaffoldState,
        onPostClicked = { post -> navigator.singleTop(Destination.ViewingPost(post)) },
        navigator = navigator)

}

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    homeFeed: List<Post> = emptyList(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onPostClicked: (post: Post) -> Unit = {},
    navigator: BackStack<Destination> = BackStack(
        initialElement = Destination.Home,
        savedStateMap = null
    )
) {


    var wantsToPost by remember {
        mutableStateOf(false)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            BottomNavigationBar(backStackNavigator = navigator, isNewNotification = false)
        },
        content = { contentPadding ->
            if (wantsToPost) NewPostView(
                onClose = {
                    wantsToPost = false
                }
            )

            Column() {
                FeedProfileImage(
                    showProfile = { navigator.push(Destination.Profile()) }
                )

                AnimatedVisibility(
                    visible = homeFeed.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        //CircularProgressIndicator(Modifier.align(Alignment.Center))
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(text = "Loading feed")
                            DotsFlashing(
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = homeFeed.isNotEmpty(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut()
                ) {
                    Content(modifier = Modifier.padding(contentPadding),
                        feed = homeFeed,
                        showProfile = {
                            navigator.push(Destination.Profile(isProfileSelected = true))
                        },
                        showPost = {
                            onPostClicked(it)
                            //navigator.navigate("selected_post")
                        })
                }

            }
        },
        floatingActionButton = {
            Fab(onTap = {
                wantsToPost = true

            })
        },
        floatingActionButtonPosition = FabPosition.End
    )

}


@Composable
fun Content(modifier: Modifier = Modifier,
            feed: List<Post> = emptyList(),
            showProfile: () -> Unit,
            showPost: (Post) -> Unit) {


    val list by remember() {
        derivedStateOf {
            PostsList(feed)
        }
    }
    val listState = rememberLazyListState()

    LazyColumn(state = listState,
            modifier = Modifier.then(modifier)){

            items(count = list.items.size){ postIndex ->
                PostView(
                    viewingPost = list.items[postIndex],
//                        .copy(
//                        textContent = "One of the user's very very long messages. from" +
//                                " 8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
//                    ),
                    isUserVerified = postIndex.mod(2) != 0,
                    containsImage = postIndex.mod(2) == 0,
                    isRelayRecommendation = postIndex == 0,
                 showProfile = showProfile, onPostClick = showPost)
                //CustomDivider()
                Spacer(modifier = Modifier.height(3.dp))
            }
        }

}

@Composable
fun Fab(onTap: () -> Unit) {
    FloatingActionButton(
        onClick = { onTap() },
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(bottom = 70.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Create,
            contentDescription = "",
            modifier = Modifier.size(25.dp)
        )
    }
}

@Composable
fun CustomDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier,
        color = if (!isSystemInDarkTheme()) Color.LightGray else Color(0xFF333333)
    )
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomePreview() {
    NoskycomposeTheme() {
        HomeView(homeFeed = opsList)
    }
}

