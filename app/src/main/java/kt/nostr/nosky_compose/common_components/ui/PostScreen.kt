package kt.nostr.nosky_compose.common_components.ui

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumble.appyx.navmodel.backstack.BackStack
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.home.backend.PostViewModel
import kt.nostr.nosky_compose.home.backend.RepliesUiState
import kt.nostr.nosky_compose.home.ui.CustomDivider
import kt.nostr.nosky_compose.home.ui.PostReply
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.profile.LoggedInProfileProvider
import kt.nostr.nosky_compose.profile.ui.ProfilePosts
import ktnostr.crypto.toBytes


//TODO : Investigate BackStack, and look for a way to not depend on it.

//TODO: Find a way to separate the state of the main post from the replies state.

@Composable
fun PostScreen(
    currentPost: Post = Post(),
    goBack: () -> Unit, navigator: BackStack<Destination>) {

    val postViewModel = viewModel<PostViewModel>()
    val repliesUiState by postViewModel.repliesUiState.collectAsState()
    val localProfile = LoggedInProfileProvider.getLoggedProfile(LocalContext.current)
//    DisposableEffect(key1 = repliesUiState){
//        postViewModel.getRepliesForPost(postId = currentPost.postId)
//        onDispose {
//            postViewModel.stopFetching()
//        }
//    }
    SideEffect() {
        postViewModel.getRepliesForPost(postId = currentPost.postId)

//        if (repliesUiState == RepliesUiState.Loading){
//
//        }
    }

    BackHandler {
        postViewModel.stopFetching()
        postViewModel.dispose()
        goBack()
    }
    val userPost = remember {
        currentPost
    }

    var isReplyClicked by remember {
        mutableStateOf(false)
    }

    if (isReplyClicked){
        PostReply(
            originalPost = userPost,
            closeDialog = { isReplyClicked =!isReplyClicked }
        ) { reply, rootEventId ->
            postViewModel.sendReply(
                privKey = localProfile.privKey.toBytes(),
                reply = reply, rootEventId = rootEventId
            )
        }
    }

    ConstraintLayout(Modifier.fillMaxSize()) {
        val (topBar, mainPost, replies, bottomBar) = createRefs()


        TopAppBar(Modifier.constrainAs(topBar) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Box(modifier = Modifier
                .fillMaxWidth()) {
                Icon(modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { goBack() },
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null)

                ThemedText(modifier = Modifier.align(Alignment.Center),
                    text = "Post",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    maxLines = 1, textColor = Color.White)

            }
        }


        Column(
            Modifier
                .constrainAs(mainPost) {
                    height = Dimension.fillToConstraints
                    top.linkTo(topBar.bottom, margin = 2.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .background(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
                )) {
            PostView(
                viewingPost = userPost,
                containsImage = false,
                onPostClick = {},
                onReplyTap = { isReplyClicked = !isReplyClicked })
            Column() {
                Text(
                    modifier = Modifier.align(CenterHorizontally), text = "Replies",
                    style = TextStyle(
                    fontSize = 18.sp,
                    color = if (isSystemInDarkTheme()) Color(0xFF666666) else Color.Gray),
                    maxLines = 1
                )
                CustomDivider(Modifier.padding(start = 15.dp, end = 15.dp))
            }
        }


        Replies(Modifier.constrainAs(replies){
            height = Dimension.fillToConstraints

            top.linkTo(mainPost.bottom)
            bottom.linkTo(bottomBar.top, margin = 10.dp)
        },
        uiState = repliesUiState,
        onForceRefresh = { postViewModel.getRepliesForPost(currentPost.postId) }
        )

        BottomNavigationBar(modifier = Modifier.constrainAs(bottomBar){
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }, backStackNavigator = navigator)
    }


}


@Composable
fun Replies(
    modifier: Modifier = Modifier,
    uiState: RepliesUiState,
    onForceRefresh: () -> Unit = {}) {

    when(uiState){
        is RepliesUiState.LoadingError -> {
            Box(modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                //CircularProgressIndicator(Modifier.align(Alignment.Center))
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(text = "Error loading replies: ${uiState.error.message}")
                    Button(
                        modifier = Modifier.align(CenterHorizontally),
                        onClick = onForceRefresh
                    ) {
                        Text(text = "Try again")
                    }
                }

                Toast.makeText(
                    LocalContext.current,
                    "Error: ${uiState.error.message}",
                    Toast.LENGTH_SHORT).show()
            }
            Log.e("NoskyApp", uiState.error.message, uiState.error)
        }
        is RepliesUiState.Loaded -> {

            if (uiState.replies.isEmpty()){
                Box(modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    //CircularProgressIndicator(Modifier.align(Alignment.Center))
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text(text = "No replies.")
                        Button(
                            modifier = Modifier.align(CenterHorizontally),
                            onClick = onForceRefresh
                        ) {
                            Text(text = "Try again")
                        }
                    }
                }
            } else {
                ProfilePosts(
                    modifier = modifier,
                    listOfPosts = uiState.replies,
                    onPostClick = {  })
            }


            Toast.makeText(
                LocalContext.current,
                "Replies loaded.",
                Toast.LENGTH_SHORT).show()
        }
        RepliesUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(),
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
    }

}

@Preview
@Composable
fun ParticularPostPreview() {
    NoskycomposeTheme(darkTheme = true) {
        Surface {
            PostScreen(goBack = {},
                navigator = BackStack(Destination.ViewingPost(), null)
            )
        }
    }
}
