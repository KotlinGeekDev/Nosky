package kt.nostr.nosky_compose.common_components.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.utility_functions.datetime.timeAgoFrom
import kt.nostr.nosky_compose.utility_functions.misc.currentSystemUnixTimeStamp
import kt.nostr.nosky_compose.utility_functions.urlsInText

@Composable
fun QuotedPost(modifier: Modifier = Modifier,
         userName: String = "Satoshi Nakamoto",
         userPubkey: String = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
         post: String = "One of the user's very very long messages. " +
                 "from 8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
         isUserVerified: Boolean = false,
         profileImageLink: String = "",
         contentImages: List<String> = post.urlsInText(),
         isPostLiked: Boolean = false,
         isPostBoosted: Boolean = false,
         onPostClick: () -> Unit,
         ) {


    Row(modifier = Modifier
        .padding(all = 10.dp)
        .clickable { onPostClick() }
        .then(modifier)) {
        Column() {
            Row {
                Avatar(
                    userImage = profileImageLink.ifBlank { R.drawable.ic_launcher_foreground }
                )
                Spacer(modifier = Modifier.size(3.dp))
                NameAndUserName(
                    userName = userName,
                    userPubkey = userPubkey, isUserVerified)
            }

            Spacer(modifier = Modifier.height(3.dp))

            TweetAndImage(post = post,
                images = if (contentImages.isEmpty()) "" else contentImages.first())
//            if (isNotMainOrNotifyPost)
//                Post(modifier = Modifier
//                    //.height(170.dp)
//                    .fillMaxWidth()
//                    .border(border = BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(15.dp)),
//                    containsImage = true,
//                    onPostClick = { })
            Spacer(modifier = Modifier.size(5.dp))
        }
        //Spacer(modifier = Modifier.size(12.dp))
    }
}

@Composable
private fun Avatar(modifier: Modifier = Modifier,
                   userImage: Any = R.drawable.ic_launcher_foreground,
                   showProfile:(() -> Unit)? = null) {


    CoilImage(
        imageModel = { userImage },
        modifier = Modifier
            .size(32.dp)
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
            //contentScale = ContentScale.Crop,
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
    publicationTime: Long = currentSystemUnixTimeStamp() - (60 * 60 *24*2)) {
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
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
        GrayText(modifier = Modifier.padding(end = 5.dp), text = " · $timeStampDiff")
    }
}

@Composable
private fun TweetAndImage(modifier: Modifier = Modifier,
                          post: String = "",
                          images: String = "") {

    val actualImage: Any = images.ifBlank { R.drawable.ic_launcher_foreground }
    ThemedText(text = post,
        style = TextStyle(fontSize = 14.sp)
    )
    Spacer(modifier = Modifier.height(10.dp))
    if (images.isNotBlank()) {
        CoilImage(
            imageModel = { actualImage },
            modifier = Modifier
                //.height(100.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(2.dp)),
            imageOptions = ImageOptions(
                contentDescription = "",
                contentScale = ContentScale.Fit
            )
        )
    }
}

@Preview()
@Composable
fun QuotedPostPreview() {
    NoskycomposeTheme {
        Surface {
            QuotedPost() {}
        }
    }
}