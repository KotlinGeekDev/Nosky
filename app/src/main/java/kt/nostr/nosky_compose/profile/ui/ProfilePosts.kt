package kt.nostr.nosky_compose.profile.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kt.nostr.nosky_compose.common_components.models.PostList
import kt.nostr.nosky_compose.common_components.ui.PostView
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.home.backend.opsList
import ktnostr.crypto.toBytes
import nostr.postr.toNpub

@Composable
fun ProfilePosts(modifier: Modifier = Modifier,
                 listOfPosts: List<Post> = opsList,
                 listState: LazyListState = rememberLazyListState(),
                 onPostClick: (Post) -> Unit) {

    val list by remember() {
        derivedStateOf {
            PostList(listOfPosts)
        }
    }

    LazyColumn(state = listState, modifier = Modifier.padding(top = 10.dp).then(modifier)) {
        itemsIndexed(items = list.items, key = { index: Int, post: Post -> post.postId + index }) { index, post ->
            PostView(
                viewingPost = post.copy(
                    user = post.user
                        .copy(pubKey = post.user.pubKey.toBytes().toNpub())
                ),
                isUserVerified = false,
                onPostClick = onPostClick
            )
            //CustomDivider()
            //Spacer(modifier = Modifier.height(3.dp))
        }
    }
}