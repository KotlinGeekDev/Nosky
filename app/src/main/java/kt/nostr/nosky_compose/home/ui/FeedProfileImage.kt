package kt.nostr.nosky_compose.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.common_components.ui.ThemedText

@Composable
fun FeedProfileImage(loggedInProfileImage: String = "", showProfile: () -> Unit){

    val profileImage: Any = remember {
        loggedInProfileImage.ifBlank { R.drawable.nosky_logo }
    }
    
    TopAppBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center){
//            Image(painter = painterResource(id = R.drawable.nosky_logo),
//                contentDescription = "App Logo",
//                Modifier
//                    .clip(CircleShape)
//                    .size(50.dp)
//                    .background(Color.Cyan)
//                    .border(1.dp, Color.Transparent, CircleShape)
//                    .aspectRatio(1f)
//                    .align(Alignment.CenterStart)
//                    .clickable(onClick = showProfile))

            CoilImage(
                imageModel = { profileImage },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp)
                 //   .background(Color.Cyan)
                    .border(1.dp, Color.White, CircleShape)
                    .aspectRatio(1f)
                    .align(Alignment.CenterStart)
                    .clickable(onClick = showProfile),
                imageOptions = ImageOptions(
                    contentDescription = "App Logo"
                )
            )

            ThemedText(modifier = Modifier.align(Alignment.Center),
                text = "Feed",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                maxLines = 1, textColor = Color.White)
        }


    }



}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun FeedProfilePreview() {
    NoskycomposeTheme {
        Surface {
            FeedProfileImage(showProfile = {})
        }
    }
}

