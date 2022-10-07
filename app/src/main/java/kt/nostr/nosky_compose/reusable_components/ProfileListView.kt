package kt.nostr.nosky_compose.reusable_components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.home.ui.CustomDivider
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme


@Composable
fun ProfileListView(goBack: () -> Unit) {

    BackHandler {
        goBack()
    }

    val userList by remember {
        mutableStateOf(List(15){
            User("Satoshi Nakamoto", "satoshi",
                "A pseudonymous dev working on iubigsubtieybgeuygbeiygbtgyibei", 10, 100_000)
        })
    }
    Scaffold(topBar = {
        AppTopBar(label = "Related Users", goBack = goBack)
    }) { contentPadding ->
        ListOfProfiles(modifier = Modifier.padding(contentPadding), userList = userList)
    }
}


@Composable
fun ListOfProfiles(modifier: Modifier = Modifier, userList: List<User>) {
    val names by remember {
        derivedStateOf { userList.map {  user ->
            user.name }
        }
    }
    val profileBios by remember {
        derivedStateOf { userList.map {  user ->
            user.bio }
        }
    }
    LazyColumn(modifier = Modifier
        .padding(bottom = 50.dp)
        .then(modifier)){
        items(userList.size){ profilePosition ->
            UserProfile(names[profilePosition], profileBios[profilePosition], profilePosition % 2 == 0)
            CustomDivider()
        }
    }
}

@Composable
fun UserProfile(userName: String,
                userBio: String,
                isUserVerified: Boolean = true) {

    val color = remember {
        Color(0.4392157F, 0.5019608F, 0.72156864F, 1.0F, ColorSpaces.Srgb)
    }

    val targetColor by animateColorAsState(targetValue = color)

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 5.dp, bottom = 8.dp)) {
        Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center){
            Image(painter = painterResource(id = R.drawable.nosky_logo),
                contentDescription = "App Logo",
                Modifier
                    .clip(CircleShape)
                    .size(65.dp)
                    .background(Color.Cyan)
                    .border(
                        border = BorderStroke(width = 3.dp, color = targetColor),
                        shape = CircleShape
                    )
                    .aspectRatio(1f))
        }
        Spacer(modifier = Modifier.width(5.dp))
        Column() {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ThemedText(
                    text = userName,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                )
                if (isUserVerified)
                    VerifiedUserIcon(Modifier.padding(top = 1.dp, start = 1.dp))
            }
            ThemedText(Modifier.fillMaxWidth(0.5f),
                text = userBio,
                style = TextStyle(fontSize = 14.sp),
                maxLines = 2
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .align(CenterVertically)
                .padding(start = 5.dp, end = 10.dp),
            onClick = {
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Text(text = "Follow", color = MaterialTheme.colors.primary)
        }
    }
}


@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun UserProfilePreview() {
    NoskycomposeTheme() {
        Surface {
            UserProfile(userName = "Satoshi Nakamoto",
                userBio = "A pseudonymous dev working on iubigsubtieybgeuygbeiygbtgyibei")
        }
    }
}

@Preview
@Composable
fun ProfileListPreview() {
    NoskycomposeTheme(darkTheme = true) {
        ProfileListView(goBack = {})
    }
}