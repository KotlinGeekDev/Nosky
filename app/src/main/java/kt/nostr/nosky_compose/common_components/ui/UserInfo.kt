package kt.nostr.nosky_compose.common_components.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kt.nostr.nosky_compose.profile.model.Profile

@Composable
fun UserInfo(modifier: Modifier = Modifier,
             username: String,
             userPubKey: String,
             userBio: String,
             following: Int,
             followers: Int,
             isUserVerified: Boolean = false,
             showFollowing: () -> Unit,
             showFollowers: () -> Unit) {
    Column(modifier = Modifier.wrapContentWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ThemedText(
                modifier = Modifier.weight(4f, fill = false),
                text = username,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                maxLines = 1
            )
            if (isUserVerified)
                VerifiedUserIcon(
                    Modifier
                        .padding(top = 3.dp)
                        .align(Alignment.CenterVertically)
                        .animateContentSize()
                        .weight(2f, fill = false)
                )
        }
        GrayText(modifier = Modifier.then(modifier), text = "@$userPubKey")
        if (userBio.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp).then(modifier))
            ThemedText(modifier = modifier,
                text = userBio,
                style = TextStyle(fontSize = 14.sp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.then(modifier),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(Modifier.clickable { showFollowing() }) {
                ThemedText(
                    text = "$following ",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                ThemedText(
                    text = "Following",
                    style = TextStyle(fontSize = 14.sp)
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Row(Modifier.clickable { showFollowers() }) {
                ThemedText(
                    text = "$followers ",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                ThemedText(
                    text = "Followers",
                    style = TextStyle(fontSize = 14.sp)
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}


@Preview
@Composable
fun UserInfoPreview() {
    val user = Profile(
        userName = "Satoshi Nakamoto (Gone)",
        pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        bio = "A pseudonymous dev", following = 10, followers = 100_000)
    androidx.compose.material.Surface {
        Column {
            UserInfo(
                username = user.userName,
                userPubKey = user.pubKey,
                userBio = user.bio,
                following = user.following,
                followers = user.followers,
                isUserVerified = true,
                showFollowing = {},
                showFollowers = {})
        }
    }
}