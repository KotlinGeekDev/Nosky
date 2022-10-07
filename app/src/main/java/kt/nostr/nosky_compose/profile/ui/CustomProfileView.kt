package kt.nostr.nosky_compose.reusable_components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.home.ui.CustomDivider
import kt.nostr.nosky_compose.profile.ProfileTweets
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme
import kotlin.math.min



@Composable
fun ProfileView(modifier: Modifier = Modifier,
                user: User = User("Satoshi Nakamoto",
                    "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
                    "A pseudonymous dev", 10, 100_000),
                profileSelected: Boolean = false,
                isProfileMine: Boolean = false,
                navController: NavController,
                goBack: () -> Unit) {

    val internalUser: User by remember {
        derivedStateOf {
            user
        }
    }
    BackHandler(profileSelected) {
        goBack()
    }
    var isRelatedFollowersClicked by remember {
        mutableStateOf(false)
    }

    if (isRelatedFollowersClicked){
        ProfileListView {
            isRelatedFollowersClicked = !isRelatedFollowersClicked
        }
    } else {

//        Surface {
//            Column {
//                VerticalNestedScrollView(state = rememberNestedScrollViewState(),
//                    header = {
//                        Profile(user = user, profileSelected = profileSelected,
//                            showRelatedFollowers = { isRelatedFollowersClicked = !isRelatedFollowersClicked },
//                            goBack = goBack)
//                    },
//                    content = {
//                        ProfileTweets()
//                    })
//            }
//        }

//        Surface() {
//            Column(
//                Modifier
//                    .fillMaxSize()
//                    .scrollable(scrollState, orientation = Orientation.Vertical)
//                    .offset(y = initialOffset.dp)) {
//                Profile(user = user, profileSelected = profileSelected,
//                    showRelatedFollowers = { isRelatedFollowersClicked = !isRelatedFollowersClicked },
//                    goBack = goBack)
//                ProfileTweets()
//            }
//        }
        val nestedScrollState = rememberLazyListState()
        val scrollOffset: Float = min(1f,
            1 - (nestedScrollState.firstVisibleItemScrollOffset/600f + nestedScrollState.firstVisibleItemIndex))
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { padding ->
            Column(Modifier.padding(paddingValues = padding)) {
                Profile(
                    user = internalUser,
                    profileSelected = profileSelected,
                    isProfileMine = isProfileMine,
                    showRelatedFollowers = { isRelatedFollowersClicked = !isRelatedFollowersClicked },
                    goBack = goBack, offset = scrollOffset)
                ProfileTweets(listState = nestedScrollState,
                    onPostClick = { navController.navigate("selected_post") })
            }
        }

    }



}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Profile(modifier: Modifier = Modifier,
            user: User,
            profileSelected: Boolean,
            isProfileMine: Boolean,
            showRelatedFollowers: () -> Unit,
            goBack: () -> Unit, offset: Float) {
    //List of animations
    //--Common for all components--
    val alpha by animateFloatAsState(targetValue = offset)

    //---For Avatar(or profile picture, refer to constraint ref below)---
    val imageSize by animateDpAsState(targetValue = max(50.dp, 80.dp * offset))
    val profilePictureLeftMargin by animateDpAsState(
        targetValue = min(16.dp * (1 - offset) + 16.dp, 40.dp)
    )
    val profilePictureTopMargin by animateDpAsState(
        targetValue = min(3.dp, (-40).dp * offset))

    //---For banner(or background image, refer to constraint ref below)---
    val bannerHeight by animateDpAsState(targetValue = max(0.dp, 70.dp * offset))
    //---For the follow or Edit profile button---
    val followButtonMargin by animateDpAsState(targetValue = max(1.dp, 16.dp * offset))

    //---For the profile description(refer to constraint ref below)---
    val profileDescHeight by animateDpAsState(targetValue = max(20.dp, 160.dp * offset))
    val profileNameLeftMargin by animateDpAsState(targetValue = min(80.dp , 80.dp * (1 - offset)))
    val profileNameTopMargin by animateDpAsState(targetValue = max(0.dp, 80.dp * offset))

        Box() {
            ConstraintLayout(constraintSet = ConstraintSet {
                val banner = createRefFor("banner")
                val closeButton = createRefFor("close")
                val moreButton = createRefFor("more")
                val avatar = createRefFor("avatar")
                val content = createRefFor("content")
                val followButton = createRefFor("follow")

                constrain(avatar){

                    top.linkTo(banner.bottom, margin = if (isProfileMine) (-40).dp else profilePictureTopMargin)
                    //bottom.linkTo(banner.bottom, margin = (-40).dp)
                    start.linkTo(parent.start, margin = if (isProfileMine) 16.dp else profilePictureLeftMargin)

                }

                constrain(followButton){
                    top.linkTo(banner.bottom, margin = if (isProfileMine) 16.dp else followButtonMargin)
                    end.linkTo(parent.end, margin = 16.dp)

                }

                constrain(closeButton){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }

                constrain(moreButton){
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }

                constrain(content){
                    top.linkTo(parent.top, margin = if (isProfileMine) 80.dp else profileNameTopMargin)
                    //bottom.linkTo(banner.bottom)
                    start.linkTo(banner.start, margin = if (isProfileMine) 0.dp else profileNameLeftMargin)


                }

            }) {
                Banner(Modifier.height(if (isProfileMine) 70.dp else bannerHeight), user)
                FollowButton(modifier, isProfileMine)
                if (profileSelected) TopBar(Modifier.alpha(if (isProfileMine) 1f else alpha), goBack = goBack)

                Avatar(Modifier.size(if (isProfileMine) 80.dp else imageSize), user = user)
                ProfileDescription(modifier = Modifier.height(if (isProfileMine) 160.dp else profileDescHeight),
                    alpha = if (isProfileMine) 1f else alpha,
                    user = user, showRelatedFollowers = showRelatedFollowers)
            }
        }
}

@Composable
private fun ProfileDescription(modifier: Modifier = Modifier,
                               alpha: Float = 1f,
                               user: User, showRelatedFollowers: () -> Unit) {
    val topSpacerHeight by animateDpAsState(targetValue = max(44.dp * alpha, 5.dp))
    val bottomSpacerHeight by animateDpAsState(targetValue = max(16.dp * alpha, 0.dp))
    Column(modifier = Modifier
        .layoutId("content")
        .padding(start = 16.dp, end = 16.dp, top = if (alpha == 0f) 6.dp else 0.dp)
        .then(modifier), verticalArrangement = Arrangement.SpaceAround) {
        Spacer(modifier = Modifier.height(topSpacerHeight))
        UserInfo(
            Modifier
                .padding(top = if (alpha <= 0.3f) 5.dp else 0.dp)
                .alpha(alpha),
            user = user,
            isUserVerified = true,
            showBio = true,
            showRelatedFollowers = showRelatedFollowers
        )
        Spacer(modifier = Modifier.height(bottomSpacerHeight))
        CustomDivider()
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
internal fun Avatar(modifier: Modifier = Modifier, user: User) {

    val color = remember {
        Color(0.4392157F, 0.5019608F, 0.72156864F, 1.0F, ColorSpaces.Srgb)
    }

    val targetColor by animateColorAsState(targetValue = color)

    Image(
        painterResource(id = R.drawable.nosky_logo),
        contentDescription = "Profile Image",
        modifier = Modifier
            .clip(shape = RoundedCornerShape(40.dp))
            .layoutId("avatar")
            .border(
                border = BorderStroke(width = 3.dp, color = targetColor),
                shape = CircleShape
            )
            .then(modifier),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TopBar(modifier: Modifier = Modifier, goBack:() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier
                .layoutId("close"),
            onClick = goBack) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
        }

        IconButton(modifier = Modifier
            .layoutId("more")
            .then(modifier), onClick = {}) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
        }
    }
}

@Composable
private fun FollowButton(modifier: Modifier = Modifier, isProfileMine: Boolean) {
    val buttonLabel by remember {
        derivedStateOf { if (isProfileMine) "Edit Profile" else "Follow" }
    }
    Button(
        modifier = Modifier
            .layoutId("follow")
            .then(modifier),
        onClick = {
        },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
        border = BorderStroke(1.dp, MaterialTheme.colors.primary)
    ) {
        Text(text = buttonLabel, color = MaterialTheme.colors.primary)
    }
}

@Composable
private fun Banner(modifier: Modifier = Modifier, user: User) {
    Image(
        painter = painterResource(id = R.drawable.nosky_logo),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .layoutId("banner")
            .then(modifier),
        contentScale = ContentScale.Fit
    )
}



@ExperimentalFoundationApi
@Preview
@Composable
fun CustomProfileViewPreview() {
    NoskycomposeTheme(darkTheme = true){
        ProfileView(profileSelected = false, isProfileMine = false, navController = rememberNavController(), goBack = {})
    }
}