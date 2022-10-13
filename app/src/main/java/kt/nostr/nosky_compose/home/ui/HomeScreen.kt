package kt.nostr.nosky_compose.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.navigation.NavigationItem
import kt.nostr.nosky_compose.notifications.ui.opsList
import kt.nostr.nosky_compose.reusable_components.Post
import kt.nostr.nosky_compose.reusable_components.ProfileView
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.utility_functions.misc.isPrime


@OptIn(ExperimentalFoundationApi::class)

@Composable
fun Home(modifier: Modifier = Modifier,
         scaffoldState: ScaffoldState = rememberScaffoldState(),
         navigator: NavController) {

//    val user = remember {
//        User(
//            "Satoshi Nakamoto", "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
//            "A pseudonymous dev", 10, 100_000
//        )
//    }


    var isProfileClicked by remember {
        mutableStateOf(false)
    }


    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { BottomNavigationBar(navController = navigator, isNewNotification = false) },
        content = { contentPadding ->
            if (isProfileClicked)
            ProfileView(profileSelected = isProfileClicked, navController = navigator,
                goBack =  { isProfileClicked = isProfileClicked.not() })
        else {
            Column() {
                FeedProfileImage(
                    showProfile = { navigator.navigate(NavigationItem.Profile.route) }
                )
                Content(modifier = Modifier.padding(contentPadding), scaffoldState = scaffoldState,
                    showProfile = { isProfileClicked = isProfileClicked.not() },
                    showPost = { navigator.navigate("selected_post") })
            }
        } },
        floatingActionButton = { if (!isProfileClicked) Fab(onTap = { }) },
        floatingActionButtonPosition = FabPosition.End
    )
}


@Composable
fun Content(modifier: Modifier = Modifier, scaffoldState: ScaffoldState,
            showProfile: () -> Unit, showPost: () -> Unit) {


    val list by rememberSaveable() {
        mutableStateOf(opsList)
    }
    val listState = rememberLazyListState()

    LazyColumn(state = listState,
            modifier = Modifier.then(modifier)){

            itemsIndexed(items = list, key = { index: Int, _: String -> index }){ post, _ ->
                Post(isUserVerified = post.mod(2) != 0,
                    containsImage = post.mod(2) == 0,
                    isNotMainOrNotifyPost = isPrime(post),
                post = "One of the user's very very long messages. from 8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
                 showProfile = showProfile, onPostClick = showPost)
                //CustomDivider()
                Spacer(modifier = Modifier.height(3.dp))
            }
        }

}

@Composable
fun Fab(onTap: () -> Unit) {
    FloatingActionButton(
        onClick = { onTap() },
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(bottom = 70.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Create,
            contentDescription = "",
            modifier = Modifier.size(25.dp)
        )
    }
}

@Composable
fun CustomDivider(modifier: Modifier = Modifier) {
    Divider(modifier = modifier, color = if (!isSystemInDarkTheme()) Color.LightGray else Color(0xFF333333))
}


@Preview(uiMode = UI_MODE_NIGHT_NO, showSystemUi = true)
@Composable
fun HomePreview() {
    NoskycomposeTheme(darkTheme = true) {
        Home(navigator = rememberNavController())
    }
}

