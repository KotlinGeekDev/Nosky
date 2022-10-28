package kt.nostr.nosky_compose.reusable_components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.utility_functions.datetime.timeAgoFrom
import ktnostr.currentUnixTimeStampFromInstant

/**
 * TODO:
 *  - Replace Image composables with CoilImage or other URL-supporting Image composables.
 *  - Replace a lot of state-representing variables(e.g boosts and likes) and
 *    hoist them.
 *  - Contemplate navigation for if a quoted post is present.
 *  - Look to improve performance.
 */


@Composable
fun Post(modifier: Modifier = Modifier,
         userName: String = "Satoshi Nakamoto",
         userPubkey: String = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
         post: String = "One of the user's very very long messages.",
         isUserVerified: Boolean = true,
         containsImage: Boolean = false,
         isPostLiked: Boolean = false,
         isPostBoosted: Boolean = false,
         isRelayRecommendation: Boolean = false,
         isNotMainOrNotifyPost: Boolean = false,
         onPostClick: () -> Unit,
         showProfile: (() -> Unit)? = null) {
    var likes by remember {
        mutableStateOf(0)
    }
    var numBoosts by remember {
        mutableStateOf(0)
    }
    var postLiked by remember {
        mutableStateOf(isPostLiked)
    }
    var postBoosted by remember {
        mutableStateOf(isPostBoosted)
    }

    val color = remember {
        Color(0.4392157F, 0.5019608F, 0.72156864F, 1.0F, ColorSpaces.Srgb)
    }

    val targetColor by animateColorAsState(targetValue = color)

    Row(modifier = Modifier
        .padding(all = 10.dp)
        .clickable { onPostClick() }
        .then(modifier)) {
        Column() {
            UserAvatar(
                modifier = Modifier.border(3.dp, color = targetColor, CircleShape),
                userImage = painterResource(id = R.drawable.nosky_logo),
                showProfile = showProfile
            )
            Spacer(modifier = Modifier.height(5.dp))
//            if (isNotMainOrNotifyPost) {
//                Divider(
//                    Modifier
//                        .width(2.dp)
//                        .wrapContentHeight()
//                        .align(Alignment.CenterHorizontally),
//                    color = MaterialTheme.colors.onSurface, thickness = 10.dp
//                )
//            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        Column {
            //GrayText(text = "You liked")
            NameAndUserName(
                userName = userName,
                userPubkey = userPubkey, isUserVerified, showProfile = showProfile
            )
            Spacer(modifier = Modifier.size(1.dp))
            TweetAndImage(post = post, containsImage = containsImage)
            if (isNotMainOrNotifyPost)
                QuotedPost(modifier = Modifier
                    //.fillMaxWidth()
                    .border(
                        border = BorderStroke(2.dp, MaterialTheme.colors.onSurface),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(all = 5.dp),
                    containsImage = true,
                    onPostClick = { onPostClick() })
            Spacer(modifier = Modifier.height(3.dp))
            if (isRelayRecommendation) {
                Card(
                    elevation = 6.dp, modifier = Modifier.height(45.dp),
                    contentColor = MaterialTheme.colors.surface,
                    shape = MaterialTheme.shapes.small,
                    backgroundColor = SnackbarDefaults.backgroundColor
                ) {
                    Row() {
                        Text(text = "New relay has been recommended. Tap to add.",
                            modifier = Modifier
                                .fillMaxWidth(0.76f)
                                .padding(3.dp)
                                .alignBy { it.measuredHeight / 2 })
                        Button(
                            onClick = { /*TODO*/ },
                            Modifier.padding(end = 5.dp)
                        ) {
                            Text(
                                text = "Add",
                                lineHeight = 4.sp,
                                textAlign = TextAlign.Center,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                //TODO: Use Snackbar when I understand and solve the error: java.lang.IllegalArgumentException: No baselines for text
//                Snackbar(modifier = Modifier.padding(4.dp), action = {
//                    TextButton(onClick = {  }) {
//                        Text(text = "Add")
//                    }
//                }) {
//                    //Text(text = "New relay found.")
//                }
            }
            TweetActions(numLikes = likes, numberOfBoosts = numBoosts,
                isPostLiked = postLiked,
                isPostBoosted = postBoosted,
                onPostLike = {
                    if (postLiked) {
                        likes -= 1
                        postLiked = !postLiked
                    } else {
                        likes += 1
                        postLiked = !postLiked
                    }
                },
                onPostBoost = {
                    if (postBoosted) {
                        numBoosts -= 1
                        postBoosted = !postBoosted
                    } else {
                        numBoosts += 1
                        postBoosted = !postBoosted
                    }
                }
            )
        }
    }
}

@Composable
private fun UserAvatar(modifier: Modifier = Modifier,
                   userImage: Painter = rememberVectorPainter(image = Icons.Default.Person),
                   showProfile:(() -> Unit)? = null) {

    Image(
        painter = userImage,
        contentDescription = "",
        modifier = Modifier
            .size(50.dp)
            .clip(shape = RoundedCornerShape(25.dp))
            .then(modifier)
            //.background(Color.Cyan)
            .aspectRatio(1f)
            .clickable {
                if (showProfile != null) {
                    showProfile()
                }
            },
        contentScale = ContentScale.Fit,
        //colorFilter = ColorFilter.tint(LocalContentColor.current.copy(alpha = LocalContentAlpha.current))
    )

}

@Composable
private fun NameAndUserName(userName: String = "", userPubkey: String = "",
                            isUserVerified: Boolean = false,
                            showProfile: (() -> Unit)? = null,
                            publicationTime: Long = currentUnixTimeStampFromInstant() - (60 * 60 *24*2)) {
    val userProfile by remember {
        derivedStateOf { Pair(userName, userPubkey ) }
    }
    val userIsVerified by remember {
        derivedStateOf { isUserVerified }
    }

    val timeStampDiff by remember {
        derivedStateOf { timeAgoFrom(publicationTime) }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        ThemedText(
            modifier = Modifier.clickable {
                if (showProfile != null) {
                    showProfile()
                }
            },
            text = userProfile.first,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            maxLines = 1,

            )
        if (userIsVerified) {
            Spacer(modifier = Modifier.size(2.dp))
            VerifiedUserIcon()
        }
        Spacer(modifier = Modifier.size(2.dp))
        GrayText(modifier = Modifier.fillMaxWidth(0.75f), text = "@${userProfile.second}")
        GrayText(text = " · ${timeStampDiff}")
    }
}

@Composable
private fun TweetAndImage(modifier: Modifier = Modifier,
                          post: String = "",
                          containsImage: Boolean = false) {
    ThemedText(
        text = post,
        style = TextStyle(fontSize = 14.sp)
    )
    Spacer(modifier = Modifier.height(10.dp))
    if (containsImage) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "",
            modifier = Modifier
                .height(170.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop
        )
    }

}

@Composable
internal fun TweetActions(numberOfComments: Int = 0,
                         numberOfBoosts: Int = 0,
                         numLikes: Int = 0,
                         isPostBoosted: Boolean,
                         isPostLiked: Boolean,
                         onPostBoost:() -> Unit,
                         onPostLike:() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val imageSize = 18.dp
        val likeIcon = if (isPostLiked) R.drawable.ic_liked else R.drawable.ic_like
        val boostIcon = if (isPostBoosted) R.drawable.ic_retweeted else R.drawable.ic_retweet
        Row {
            Icon(
                imageVector = Icons.Default.Comment,
                contentDescription = "",
                modifier = Modifier.size(imageSize)
            )
            Spacer(modifier = Modifier.size(4.dp))
            GrayText(text = numberOfComments.toString())
        }

        Row(modifier = Modifier) {
            Image(
                painter = painterResource(id = likeIcon),
                contentDescription = "",
                modifier = Modifier
                    .size(imageSize)
                    .clickable { onPostLike() }
            )

            Spacer(modifier = Modifier.size(4.dp))
            GrayText(text = numLikes.toString())
        }

        Row(modifier = Modifier) {
            Image(
                painter = painterResource(id = boostIcon),
                contentDescription = "",
                modifier = Modifier
                    .size(imageSize)
                    .clickable { onPostBoost() }
            )
            Spacer(modifier = Modifier.size(4.dp))
            GrayText(text = numberOfBoosts.toString())
        }

        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "",
            modifier = Modifier
                .size(imageSize)
                .clickable { }
        )
    }
}

@Preview
@Composable
fun PostPreview() {
    NoskycomposeTheme(darkTheme = true) {
        Surface() {
            Post(containsImage = true,
                isRelayRecommendation = true,
                isNotMainOrNotifyPost = true,
                onPostClick = { })
        }
    }
}