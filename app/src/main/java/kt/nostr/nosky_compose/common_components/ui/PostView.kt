package kt.nostr.nosky_compose.common_components.ui

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Reply
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import coil.request.CachePolicy
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.glide.GlideImage
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.home.backend.User
import kt.nostr.nosky_compose.utility_functions.datetime.timeAgoFrom
import kt.nostr.nosky_compose.utility_functions.misc.currentSystemUnixTimeStamp

/**
 * TODO:
 *  - Replace a lot of state-representing variables(e.g boosts and likes) and
 *    hoist them.
 *  - Contemplate navigation for if a quoted post is present.
 *
 */


@Composable
fun PostView(modifier: Modifier = Modifier,
             viewingPost: Post = Post(
                 user = User(
                     username = "Satoshi Nakamoto",
                     pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
                 ),
                 textContent = "One of the @user user's very very long messages with tag #new. containing a link to nostr.com",
                 quotedPost = Post()
             ),
             isUserVerified: Boolean = false,
             containsImage: Boolean = false,
             isPostLiked: Boolean = false,
             isPostBoosted: Boolean = false,
             isRelayRecommendation: Boolean = false,
             onPostClick: (Post) -> Unit = {},
             onReplyTap: () -> Unit = {},
             showProfile: (() -> Unit)? = null) {
//    var likes by remember {
//        mutableStateOf(0)
//    }
//    var numBoosts by remember {
//        mutableStateOf(0)
//    }
//    var postLiked by remember {
//        mutableStateOf(isPostLiked)
//    }
//    var postBoosted by remember {
//        mutableStateOf(isPostBoosted)
//    }


    Row(modifier = Modifier
        .padding(all = 10.dp)
        .clickable { onPostClick(viewingPost) }
        .then(modifier)) {
        Column() {
            UserAvatar(
                userImage = viewingPost.user.image,
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
                userName = viewingPost.user.username,
                userPubkey = viewingPost.user.pubKey,
                isUserVerified,
                publicationTime = viewingPost.timestamp,
                showProfile = showProfile
            )
            Spacer(modifier = Modifier.size(1.dp))
            TweetAndImage(post = viewingPost.textContent, imageLinks = viewingPost.imageLinks)
            if (viewingPost.quotedPost != null)
                QuotedPost(modifier = Modifier
                    .border(
                        border = BorderStroke(2.dp, MaterialTheme.colors.onSurface),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(all = 2.dp)
                    .fillMaxWidth(),
                    userName = viewingPost.quotedPost.user.username,
                    userPubkey = viewingPost.quotedPost.user.pubKey,
                    post = viewingPost.quotedPost.textContent,
                    onPostClick = { onPostClick(viewingPost.quotedPost) })

            if (isRelayRecommendation) {
               //CustomRelayRecommendation()
                RelayRecommendation()
                Spacer(modifier = Modifier.height(5.dp))
            }
            TweetActions(
//                numLikes = likes,
//                numberOfBoosts = numBoosts,
//                isPostLiked = postLiked,
//                isPostBoosted = postBoosted,
//                onPostLike = {
//                    if (postLiked) {
//                        likes -= 1
//                        postLiked = !postLiked
//                    } else {
//                        likes += 1
//                        postLiked = !postLiked
//                    }
//                },
//                onPostBoost = {
//                    if (postBoosted) {
//                        numBoosts -= 1
//                        postBoosted = !postBoosted
//                    } else {
//                        numBoosts += 1
//                        postBoosted = !postBoosted
//                    }
//                },
                onPostReply = onReplyTap
            )
        }
    }
}

@Composable
private fun UserAvatar(modifier: Modifier = Modifier,
                   userImage: String = "",
                   showProfile:(() -> Unit)? = null) {

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .allowHardware(false)
        .components {
            if (Build.VERSION.SDK_INT >= 28){
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
            add(SvgDecoder.Factory())
            add(VideoFrameDecoder.Factory())
        }
        .build()

    val loadedImage: Any = remember {
        userImage.ifBlank { R.drawable.ic_launcher_foreground }
    }

//    GlideImage(imageModel = { loadedImage },
//        modifier = Modifier
//            .size(50.dp)
//            .clip(shape = RoundedCornerShape(25.dp))
//            .border(3.dp, color = Color.Gray, CircleShape)
//            .then(modifier)
//            //.background(Color.Cyan)
//            .aspectRatio(1f)
//            .clickable {
//                if (showProfile != null) {
//                    showProfile()
//                }
//            },
//        imageOptions = ImageOptions(
//            contentScale = ContentScale.Fit,
//            contentDescription = ""
//        ),
//        glideRequestType = GlideRequestType.BITMAP,
//        requestOptions = {
//            RequestOptions().onlyRetrieveFromCache(true)
//        }
//    )

    CoilImage(
        imageModel = { loadedImage },
        imageLoader = { imageLoader },
        modifier = Modifier
            .size(50.dp)
            .clip(shape = RoundedCornerShape(25.dp))
            .border(3.dp, color = Color.Gray, CircleShape)
            .then(modifier)
            //.background(Color.Cyan)
            .aspectRatio(1f)
            .clickable {
                if (showProfile != null) {
                    showProfile()
                }
            },
        imageOptions = ImageOptions(
            contentScale = ContentScale.Fit,
            contentDescription = ""
        )

    )

}

@Composable
private fun NameAndUserName(
    userName: String = "",
    userPubkey: String = "",
    isUserVerified: Boolean = false,
    showProfile: (() -> Unit)? = null,
    publicationTime: Long = currentSystemUnixTimeStamp() - (60 * 60 *24*2)
) {
    val userProfile by remember {
        derivedStateOf { Pair(userName, userPubkey ) }
    }
    val userIsVerified by remember {
        derivedStateOf { isUserVerified }
    }

    val timeStampDiff by remember {
        derivedStateOf { timeAgoFrom(publicationTime) }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        GrayText(modifier = Modifier.weight(1f), text = "@${userProfile.second}")
        GrayText(text = " · $timeStampDiff")
    }
}

//TODO: Add handling for rendering content if it is an image.
@Composable
private fun TweetAndImage(modifier: Modifier = Modifier,
                          post: String = "",
                          imageLinks: List<String> = emptyList()) {

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .components {
            if (Build.VERSION.SDK_INT >= 28){
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
            add(SvgDecoder.Factory())
            add(VideoFrameDecoder.Factory())
        }
        .build()
    Column {
        LinkifyText(
            text = post,
            style = TextStyle(fontSize = 14.sp),
            color = MaterialTheme.colors.onSurface,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(3.dp))
        if (imageLinks.isNotEmpty()) {
            GlideImage(
                imageModel = { imageLinks.first() },
                modifier = Modifier
//                    .height(170.dp)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(2.dp)),
                imageOptions = ImageOptions(
                    contentDescription = "",
                    contentScale = ContentScale.Fit
                )
            )

//            CoilImage(
//                imageModel = { imageLinks.first() },
//                imageLoader = { imageLoader },
//                modifier = Modifier
////                    .height(170.dp)
//                    .fillMaxWidth()
//                    .clip(shape = RoundedCornerShape(2.dp)),
//                imageOptions = ImageOptions(
//                    contentDescription = "",
//                    contentScale = ContentScale.Fit
//                ),
//                previewPlaceholder = R.drawable.ic_launcher_foreground
//            )
        } else {
            Spacer(modifier = Modifier.size(1.dp))
        }
    }

}

@Composable
internal fun TweetActions(numberOfComments: Int = 0,
//                         numberOfBoosts: Int = 0,
//                         numLikes: Int = 0,
//                         isPostBoosted: Boolean,
//                         isPostLiked: Boolean,
//                         onPostBoost:() -> Unit,
//                         onPostLike:() -> Unit,
                          onPostReply:() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val imageSize = 20.dp
//        val likeIcon = if (isPostLiked) R.drawable.ic_liked else R.drawable.ic_like
//        val boostIcon = if (isPostBoosted) R.drawable.ic_retweeted else R.drawable.ic_retweet
        Row {
            Icon(
                imageVector = Icons.Default.Comment,
                contentDescription = "",
                modifier = Modifier.size(imageSize)
            )
            Spacer(modifier = Modifier.size(4.dp))
            GrayText(text = numberOfComments.toString())
        }

//        Row(modifier = Modifier) {
//            Image(
//                painter = painterResource(id = likeIcon),
//                contentDescription = "",
//                modifier = Modifier
//                    .size(imageSize)
//                    .clickable { onPostLike() }
//            )
//
//            Spacer(modifier = Modifier.size(4.dp))
//            GrayText(text = numLikes.toString())
//        }
//
//        Row(modifier = Modifier) {
//            Image(
//                painter = painterResource(id = boostIcon),
//                contentDescription = "",
//                modifier = Modifier
//                    .size(imageSize)
//                    .clickable { onPostBoost() }
//            )
//            Spacer(modifier = Modifier.size(4.dp))
//            GrayText(text = numberOfBoosts.toString())
//        }

        Icon(
            imageVector = Icons.Default.Reply,
            contentDescription = "",
            modifier = Modifier
                .size(imageSize)
                .clickable { onPostReply() }
        )
    }
}

@Preview(
//    device = Devices.DESKTOP
)
@Composable
fun PostPreview() {
    NoskycomposeTheme(darkTheme = true) {
        Surface() {
            PostView(
                containsImage = true,
                isRelayRecommendation = true,
                //containsQuotedPost = true,
                onPostClick = { })
        }
    }
}