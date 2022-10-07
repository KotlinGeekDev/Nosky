package kt.nostr.nosky_compose.direct_messages.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.direct_messages.Models.MessageItem
import kt.nostr.nosky_compose.direct_messages.Models.messageList
import kt.nostr.nosky_compose.reusable_components.AppTopBar
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme


@Composable
fun DiscussionScreen(navigator: NavController) {


    Scaffold(topBar = {
        AppTopBar(label = "Conversation") {
            navigator.navigateUp()
        }
    }, floatingActionButton = {
        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "New message")
        }
    },
        bottomBar = {
            BottomNavigationBar(navController = navigator)
        }) { paddingConstraints ->
        LazyColumn(Modifier.padding(paddingConstraints)) {
            items(messageList){ item: MessageItem ->
                MessageBubble(message = item)
            }
        }
    }

}

@Preview
@Preview(showSystemUi = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DiscussionScreenPreview(){
    NoskycomposeTheme() {
        DiscussionScreen(rememberNavController())
    }
}