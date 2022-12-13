package kt.nostr.nosky_compose.common_components.models

import androidx.compose.runtime.Stable
import kt.nostr.nosky_compose.home.backend.Post


@Stable
class PostList(val items: List<Post>)