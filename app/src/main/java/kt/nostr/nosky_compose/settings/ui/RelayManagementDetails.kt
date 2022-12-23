package kt.nostr.nosky_compose.settings.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.common_components.ui.AppTopBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RelayManagementSettingsScreen(returnToMainPage: () -> Unit = {}) {

    val mutableRelayList by remember {
        mutableStateOf(relayList.toMutableList())
    }

    Scaffold(
     topBar = {
         AppTopBar(label = "Configure relays", goBack = returnToMainPage)
     },
     bottomBar = {

     },
     floatingActionButton = {

     }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            items(mutableRelayList, key = { item: NostrRelay -> item.url }){ relay ->
                RelayComponent(
                    modifier = Modifier.animateItemPlacement(),
                    relayUrl = relay.url,
                    readPolicy = relay.readPolicy,
                    writePolicy = relay.writePolicy,
                    onDelete = { mutableRelayList.remove(relay) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

        }
    }
}

@Composable
fun RelayComponent(
    modifier: Modifier = Modifier,
    relayUrl: String = "",
    readPolicy: Boolean = true,
    writePolicy: Boolean = true,
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(horizontal = 5.dp)
            .height(70.dp)
            .fillMaxWidth(),
        elevation = 5.dp,
        shape = RoundedCornerShape(percent = 10),
        backgroundColor = MaterialTheme.colors.onSurface
                .copy(alpha = 0.1f)
                .compositeOver(MaterialTheme.colors.surface)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.padding(start = 5.dp),
                verticalAlignment = CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(8.dp)
                        .padding(top = 2.dp),
                    tint = Color.Green
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = relayUrl,
                    modifier = Modifier.weight(1f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete relay",
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(horizontal = 5.dp)
                        .size(25.dp)
                        .clickable { onDelete() }
                )
            }

        }

//        Row(
//            modifier = Modifier.padding(start = 20.dp, bottom = 2.dp),
//            verticalAlignment = Alignment.Bottom
//        ) {
//            Text(text = "Read-only", modifier = Modifier)
//        }

    }
}

@Stable
class NostrRelay(val url: String, val readPolicy: Boolean, val writePolicy: Boolean)

val relayList = listOf(
    NostrRelay("wss://nostr-relay.untethr.me", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr-relay.freeberty.net", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr.bitcoiner.social", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr-relay.wlvs.space", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr-pub.wellorder.net", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr.rocks", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr.onsats.org", readPolicy = true, writePolicy = true),
    NostrRelay("wss://relay.damus.io", readPolicy = true, writePolicy = true)
)

@Preview
@Composable
fun RelayComponentPreview() {

    NoskycomposeTheme {
        RelayComponent(
            relayUrl = "wss://nostr-relay.freeberty.net"
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun RelayManagementScreenPreview() {
    NoskycomposeTheme {
        RelayManagementSettingsScreen()
    }
}