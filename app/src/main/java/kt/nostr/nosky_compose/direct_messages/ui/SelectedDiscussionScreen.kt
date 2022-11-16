package kt.nostr.nosky_compose.direct_messages.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.navmodel.backstack.BackStack
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.direct_messages.Models.MessageItem
import kt.nostr.nosky_compose.direct_messages.Models.messageList
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.reusable_ui_components.AppTopBar
import kt.nostr.nosky_compose.reusable_ui_components.ThemedText
import kt.nostr.nosky_compose.reusable_ui_components.VerifiedUserIcon
import kt.nostr.nosky_compose.reusable_ui_components.theme.NoskycomposeTheme


@Composable
fun DiscussionScreen(navigator: BackStack<Destination>, goBack: () -> Unit = {}) {


    Scaffold(topBar = {
        AppTopBar(header = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ThemedText(
                    text = "Profile just a bunch of additional text",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    maxLines = 1,
                    textColor = Color.White,
                    modifier = Modifier.weight(7f, fill = false)
                )
                Spacer(modifier = Modifier.width(3.dp))
                //if (isUserVerified)
                VerifiedUserIcon(
                    Modifier
                        .padding(top = 1.dp, start = 1.dp)
                        .fillMaxWidth()
                        .weight(5f, fill = false)
                )
            }

        }) {
//            navigator.handleUpNavigation()
            goBack()
        }
    }, floatingActionButton = {
        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            onClick = {}
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "New message")
        }
    },
        bottomBar = {
            BottomNavigationBar(backStackNavigator = navigator)
        }
    ) { paddingConstraints ->

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
        DiscussionScreen(navigator = BackStack(
            initialElement = Destination.Home,
            savedStateMap = null
        ))
    }
}