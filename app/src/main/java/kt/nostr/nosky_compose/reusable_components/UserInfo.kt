package kt.nostr.nosky_compose.reusable_components

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserInfo(modifier: Modifier = Modifier,
             user: User,
             isUserVerified: Boolean = false,
             showBio: Boolean = false,
             showRelatedFollowers: () -> Unit) {
    Row(verticalAlignment = CenterVertically) {
        ThemedText(
            text = user.name,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
        )
        if (isUserVerified)
            VerifiedUserIcon(Modifier.padding(start = 2.dp, top = 3.dp).align(CenterVertically))
    }
    GrayText(modifier = Modifier.then(modifier), text = "@${user.username}")
    if (showBio && user.bio.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp).then(modifier))
        ThemedText(modifier = modifier,
            text = user.bio,
            style = TextStyle(fontSize = 14.sp)
        )
    }
    Spacer(modifier = Modifier.height(8.dp).then(modifier))
    Row(modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = CenterVertically
    ) {
        Row(Modifier.clickable { showRelatedFollowers() }) {
            ThemedText(
                text = "${user.following} ",
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            ThemedText(
                text = "Following",
                style = TextStyle(fontSize = 14.sp)
            )
        }
        Spacer(modifier = Modifier.width(24.dp))
        Row(Modifier.clickable { showRelatedFollowers() }) {
            ThemedText(
                text = "${user.followers} ",
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            ThemedText(
                text = "Followers",
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}

@kotlinx.parcelize.Parcelize
data class User(val name: String, val username: String, val bio: String,
                val following: Int, val followers: Int): Parcelable

@Preview
@Composable
fun UserInfoPreview() {
    val user = User("Satoshi Nakamoto", "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        "A pseudonymous dev", 10, 100_000)
    androidx.compose.material.Surface {
        Column {
            UserInfo(user = user, isUserVerified = true, showBio = true, showRelatedFollowers = {})
        }
    }
}