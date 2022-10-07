package kt.nostr.nosky_compose.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kt.nostr.nosky_compose.notifications.ui.opsList
import kt.nostr.nosky_compose.reusable_components.Post

@Composable
fun ProfileTweets(modifier: Modifier = Modifier,
                  listState: LazyListState = rememberLazyListState(), onPostClick: () -> Unit) {

    LazyColumn(state = listState, modifier = Modifier.padding(top = 10.dp).then(modifier)) {
        itemsIndexed(items = opsList, key = { index: Int, _: String -> index }) { post, _ ->
            Post(
                isUserVerified = post.mod(2) != 0,
                containsImage = post.mod(2) == 0,
                post = "One of the user's very very long messages. " +
                        "from 8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
                onPostClick = onPostClick
            )
            //CustomDivider()
            //Spacer(modifier = Modifier.height(3.dp))
        }
    }
}