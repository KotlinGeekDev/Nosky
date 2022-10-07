@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package kt.nostr.nosky_compose.direct_messages.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.direct_messages.Models.Person
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme


@Composable
fun Discussions(navController: NavController) {


    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController,
            //    isNewNotification = false
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(paddingValues = padding)){

            items(15){ index ->
                DiscussionCard(userName = index.toString(), isLatestMessageMine = index.mod(2) == 0,
                    onDiscussionClick = {
                    navController.navigate("message"){

                        navController.currentDestination?.route?.let { route ->

                            popUpTo(route){
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
                Divider(thickness = Dp.Hairline)
            }
        }
    }
}

@Composable
fun DiscussionElement(user: Person) {

}

@Composable
private fun DiscussionCard(modifier: Modifier = Modifier,
                   userName: String,
                   //latestMessage: String,
                   isLatestMessageMine: Boolean,
                   //messageTime: DateFormat,
                    onDiscussionClick: () -> Unit
) {

            Row(
                Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .clickable { onDiscussionClick() }) {

                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User Image Profile",
                        modifier = Modifier.clip(CircleShape).size(56.dp)
                    )

                Column(Modifier
                    .padding(start = 5.dp)
                    .weight(7f)) {
                    Text(text = "User name $userName", style = MaterialTheme.typography.subtitle2,
                        fontSize = 18.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)

                    Text(text = "One of the user's very very long messages.",
                        style = MaterialTheme.typography.subtitle1,
                        overflow = TextOverflow.Ellipsis, maxLines = 1,
                        fontSize = 16.sp)
                }

                Column(modifier = Modifier.weight(2f),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Yesterday", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isLatestMessageMine)
                        Icon(imageVector = Icons.Default.DoneAll, contentDescription = "")
                    else
                        Icon(imageVector = Icons.Default.Check, contentDescription = "")

                }

            }

}



@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DiscussionCardPreview() {
    NoskycomposeTheme {
        Surface {
            DiscussionCard(userName = "Me", isLatestMessageMine = true, onDiscussionClick = {})
        }
    }
}

//@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DiscussionsPreview() {
    NoskycomposeTheme {
        Discussions(rememberNavController())
    }
}