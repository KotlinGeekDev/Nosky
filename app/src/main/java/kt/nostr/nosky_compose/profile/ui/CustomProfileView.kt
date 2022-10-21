@file:OptIn(ExperimentalMotionApi::class)

package kt.nostr.nosky_compose.reusable_components

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.constraintlayout.compose.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.profile.ProfileTweets
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme
import kotlin.math.min


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileView(modifier: Modifier = Modifier,
                user: User = User("Satoshi Nakamoto (Gone)",
                    "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
                    "A pseudonymous dev", 10, 100_000),
                profileSelected: Boolean = false,
                isProfileMine: Boolean = !profileSelected,
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


        val nestedScrollState = rememberScrollState(0)
        val delta = with(LocalDensity.current){ 160.dp.toPx() - 30.dp.toPx() }

        val scrollState = rememberLazyListState(
            initialFirstVisibleItemIndex = nestedScrollState.value,
            initialFirstVisibleItemScrollOffset = nestedScrollState.value
        )

        val scrollOffset: Float by remember {
            derivedStateOf {
                min(1f, scrollState.firstVisibleItemIndex*100/delta)
               // min(nestedScrollState.value/delta, 1f)

            }
        }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { padding ->
            Column(
                Modifier
                    .padding(paddingValues = padding)
//                    .scrollable(state = nestedScrollState,
//                        orientation = Orientation.Vertical, reverseDirection = true),
            ) {
                Profile(
                    user = internalUser,
                    profileSelected = profileSelected,
                    isProfileMine = isProfileMine,
                    showRelatedFollowers = { isRelatedFollowersClicked = !isRelatedFollowersClicked },
                    goBack = goBack, offsetProvider = { scrollOffset })
                ProfileTweets(
                    listState = scrollState,
                    onPostClick = { navController.navigate("selected_post") })
            }
        }

    }



}

/**
 * TODO: Replace current Profile View layout
 *  with this layout below.
  */
//@Composable
//fun ModifiedProfile(modifier: Modifier = Modifier,
//                    user: User,
//                    profileSelected: Boolean,
//                    isProfileMine: Boolean,
//                    showRelatedFollowers: () -> Unit,
//                    goBack: () -> Unit, offsetProvider: () -> Float) {
//    val startContraints = ConstraintSet {
//
//        val banner = createRefFor("banner")
//        val closeButton = createRefFor("close")
//        val moreButton = createRefFor("more")
//        val avatar = createRefFor("avatar")
//        val content = createRefFor("content")
//        val followButton = createRefFor("follow")
//
//
//        constrain(avatar){
//
//            top.linkTo(banner.bottom, margin = (-40).dp)
//            //bottom.linkTo(banner.bottom, margin = (-40).dp)
//            start.linkTo(parent.start, margin = 16.dp)
//
//        }
//
//        constrain(followButton){
//            top.linkTo(banner.bottom, margin = 16.dp)
//            end.linkTo(parent.end, margin = 16.dp)
//
//        }
//
//        constrain(closeButton){
//            top.linkTo(parent.top)
//            start.linkTo(parent.start)
//        }
//
//        constrain(moreButton){
//            top.linkTo(parent.top)
//            end.linkTo(parent.end)
//        }
//        constrain(content){
//            top.linkTo(parent.top, margin = 80.dp)
//            //bottom.linkTo(banner.bottom)
//            start.linkTo(banner.start, margin = 0.dp)
//
//        }
//    }
//
//    val endConstraintSet = ConstraintSet {
//        val banner = createRefFor("banner")
//        val closeButton = createRefFor("close")
//        val moreButton = createRefFor("more")
//        val avatar = createRefFor("avatar")
//        val content = createRefFor("content")
//        val followButton = createRefFor("follow")
//
//
//        constrain(avatar){
//
//            top.linkTo(banner.bottom, margin = 3.dp)
//            //bottom.linkTo(banner.bottom, margin = (-40).dp)
//            start.linkTo(parent.start, margin = 40.dp)
//
//        }
//
//        constrain(followButton){
//            top.linkTo(banner.bottom, margin = 5.dp)
//            end.linkTo(parent.end, margin = 2.dp)
//
//        }
//
//        constrain(closeButton){
//            top.linkTo(parent.top)
//            start.linkTo(parent.start)
//        }
//
//        constrain(moreButton){
//            top.linkTo(parent.top)
//            end.linkTo(parent.end)
//        }
//        constrain(content){
//            top.linkTo(parent.top, margin = 0.dp)
//            //bottom.linkTo(banner.bottom)
//            start.linkTo(banner.start, margin = 75.dp)
//
//        }
//    }
//    MotionLayout(start = startContraints,
//        end = endConstraintSet,
//        progress = offsetProvider()
//    ) {
//
//    }
//}


@SuppressLint("Range")
@Composable
fun Profile(
    modifier: Modifier = Modifier,
    user: User,
    profileSelected: Boolean,
    isProfileMine: Boolean,
    showRelatedFollowers: () -> Unit,
    goBack: () -> Unit,
    offsetProvider: () -> Float
) {
    //List of animations
    //--Common for all components--
    val alphaProvider by animateFloatAsState(targetValue = 1 - offsetProvider())

    //---For Avatar(or profile picture, refer to constraint ref below)---
    val imageSize by animateDpAsState(
        targetValue = max(50.dp, 80.dp * (1 - offsetProvider()))
    )
    val profilePictureLeftMargin by animateDpAsState(
        targetValue = lerp(start = 16.dp, stop = 40.dp, fraction = offsetProvider())
    )

    val profilePictureTopMargin by animateDpAsState(
        targetValue = min(3.dp, (-40).dp * (1 - offsetProvider()))
    )

    //---For banner(or background image, refer to constraint ref below)---
    val bannerHeight by animateDpAsState(
        targetValue = max(0.dp, 70.dp * (1 - offsetProvider()))
    )
    //---For the follow or Edit profile button---
    val followButtonMargin by animateDpAsState(
        targetValue = max(1.dp, 16.dp * (1 - offsetProvider()))
    )

    //---For the profile description(refer to constraint ref below)---
    val profileDescHeight by animateDpAsState(
        targetValue = max(30.dp, 160.dp * (1 - offsetProvider()))
    )
    val profileNameLeftMargin by animateDpAsState(
        targetValue = min(72.dp , 72.dp * offsetProvider())
    )
    val profileNameTopMargin by animateDpAsState(
        targetValue = max(0.dp, 124.dp * (1 - offsetProvider()))
    )

    ConstraintLayout(constraintSet = ConstraintSet {

        val banner = createRefFor("banner")
        val backButton = createRefFor("back")
        val moreButton = createRefFor("more")
        val avatar = createRefFor("avatar")
        val content = createRefFor("content")
        val followButton = createRefFor("follow")

        constrain(banner){
            height = Dimension.value(if (isProfileMine) 70.dp else bannerHeight)
        }


        constrain(avatar){
            height = Dimension.value(if (isProfileMine) 80.dp else imageSize)
            width = Dimension.value(if (isProfileMine) 80.dp else imageSize)
            top.linkTo(banner.bottom, margin = if (isProfileMine) (-40).dp else profilePictureTopMargin)
            start.linkTo(parent.start, margin = if (isProfileMine) 16.dp else profilePictureLeftMargin)

        }

        constrain(followButton){
            width = Dimension.wrapContent
            height = Dimension.wrapContent
            top.linkTo(banner.bottom, margin = if (isProfileMine) 16.dp else followButtonMargin)
            end.linkTo(parent.end, margin = if (isProfileMine) 16.dp else followButtonMargin)

        }

        constrain(backButton){
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        }

        constrain(moreButton){
            alpha = if (isProfileMine) 1f else alphaProvider
            top.linkTo(parent.top)
            end.linkTo(parent.end)
        }

        constrain(content){
            height = Dimension.preferredValue(if (isProfileMine) 160.dp else profileDescHeight)
            width = Dimension.wrapContent
            top.linkTo(parent.top, margin = if (isProfileMine) 124.dp else profileNameTopMargin)
            start.linkTo(parent.start, margin = if (isProfileMine) 0.dp else profileNameLeftMargin)


        }
    }, modifier = Modifier.then(modifier)) {
        Banner()
        FollowButton(isProfileMine = isProfileMine)
        if (profileSelected)
            TopBar(
//                modifier = Modifier
//                    .graphicsLayer {
//                          alpha = alphaProvider
//                    },
                goBack = goBack)

        Avatar()
        ProfileDescription(
            alpha = if (isProfileMine) 1f else alphaProvider,
            user = user, showRelatedFollowers = showRelatedFollowers
        )
    }
}

@Composable
private fun ProfileDescription(modifier: Modifier = Modifier,
                               alpha: Float = 1f,
                               user: User, showRelatedFollowers: () -> Unit) {
    Column(modifier = Modifier
        .layoutId("content")
        .padding(start = 16.dp, end = 16.dp, top = 6.dp)
        .then(modifier)) {
        UserInfo(
            Modifier.alpha(alpha),
            username = user.name,
            userPubKey = user.username,
            userBio = user.bio,
            following = user.following,
            followers = user.followers,
            isUserVerified = true,
            showRelatedFollowers = showRelatedFollowers
        )
        Divider(color = MaterialTheme.colors.onSurface)
    }
}


@Composable
internal fun Avatar(modifier: Modifier = Modifier, profileImageUrl: String = "") {

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
                .layoutId("back"),
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
        derivedStateOf { if (isProfileMine) "Edit Profile" else "Following" }
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
private fun Banner(modifier: Modifier = Modifier, profileImage: String = "") {
    //CoilImage(imageModel = user.bio, shimmerParams = ShimmerParams(), success = {})

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
        ProfileView(
            profileSelected = true,
            //profileSelected = true, isProfileMine = false,
            navController = rememberNavController(), goBack = {})
    }
}