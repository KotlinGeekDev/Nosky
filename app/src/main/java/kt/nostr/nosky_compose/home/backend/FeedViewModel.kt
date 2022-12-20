package kt.nostr.nosky_compose.home.backend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kt.nostr.nosky_compose.main_backend.NostrService
import kt.nostr.nosky_compose.utility_functions.APP_TAG
import kt.nostr.nosky_compose.utility_functions.urlsInText
import nostr.postr.events.MetadataEvent
import nostr.postr.events.TextNoteEvent
import nostr.postr.toHex
import nostr.postr.toNpub

sealed class FeedState {
    object Loading: FeedState()
    class Loaded(val feed: List<Post>): FeedState()
    object Empty: FeedState()
    class FeedError(val errorMessage: String): FeedState()
}

class FeedViewModel(): ViewModel() {

    private val nostrService = NostrService

    private val eventsCache: MutableSet<TextNoteEvent> = mutableSetOf()
    private val profiles: MutableSet<MetadataEvent> = mutableSetOf()


    private val _feedContent = MutableStateFlow<FeedState>(FeedState.Loading)
    val feedContent = _feedContent.asStateFlow()

    private val postCache: MutableSet<Post> = mutableSetOf()

//    init {
//        getUpdateFeed()
//    }

    //TODO : Replace with a call to NostrService.
    fun getUpdateFeed(){


        viewModelScope.launch {
//            if (_feedContent.value != FeedState.Loading){
//                _feedContent.update {
//                    FeedState.Loading
//                }
//            }

            if (postCache.isNotEmpty()){
                Log.i(APP_TAG, "Taking cached events instead. Cache size: ${postCache.size}")
                //val feed = postCache.distinctBy { it.postId }.sortedByDescending { it.timestamp }
                _feedContent.update { FeedState.Loaded(postCache.toList()) }
                this.cancel()
            } else {

                nostrService.getTextEvents()

                    .onCompletion { Log.i(APP_TAG,"Text events completed.") }
                    .distinctUntilChanged { old, new ->
                        old.id.contentEquals(new.id)
                    }
                    .catch { e ->
                        Log.i(APP_TAG,"Feed flow. Caught ${e.message}")
                        _feedContent.update { FeedState.FeedError(e.message?: "Could not load the posts.") }
                    }.collect {
                        eventsCache.add(it)
                        Log.i(APP_TAG,"Collected event ${it.content} ")
                    }
                Log.i(APP_TAG,"eventsCacheSize: ${eventsCache.size}")


                val pubkeys = eventsCache.map { it.pubKey.toHex() }.distinct()
                Log.i(APP_TAG,"pubkeys_Size: ${pubkeys.size}")
                nostrService.getProfilesInfo(pubkeys)
                    .onCompletion { Log.i(APP_TAG,"Profiles completed.") }
                    .catch { error ->
                        Log.e(APP_TAG,"Profiles flow. Caught ${error.message}")
                        _feedContent.update { FeedState.FeedError(error.message?: "Could mot load the profiles.") }
                    }.collect {

                        profiles.add(it)
                        Log.i(APP_TAG,"Obtained profile: Name ->${it.contactMetaData.name} Image->${it.contactMetaData.picture}")
                        //if (profiles.size == pubkeys.size) cancel()
                    }
                Log.i(APP_TAG,"profilesCacheSize: ${profiles.size}")



                if (eventsCache.isEmpty()) {
                    //cancel()
                    _feedContent.update { FeedState.Empty }
                }


                val eventsByPubkey = eventsCache
                    .distinctBy { it.id.toHex() }
                    .associateBy { it.pubKey.toHex() }

                val posts = profiles
                    //.filter { eventsByPubkey[it.pubKey.toHex()] != null }
                    .mapNotNullTo(mutableSetOf()) { metadataEvent ->
                        val metaDataKey = metadataEvent.pubKey.toHex()
                        val textEvent = eventsByPubkey[metaDataKey] ?: return@mapNotNullTo null
                        val textEventKey = textEvent.pubKey.toHex()
                        val user = User(
                            username = if (textEventKey == metaDataKey) {
                                metadataEvent.contactMetaData.name ?: textEvent.pubKey.toNpub().take(9)
                            } else {
                                textEvent.pubKey.toNpub().take(9)
                            },
                            pubKey = textEventKey,
                            bio = metadataEvent.contactMetaData.about.orEmpty(),
                            image = metadataEvent.contactMetaData.picture.orEmpty()
                        )

                        Post(
                            user = user,
                            postId = textEvent.id.toHex(),
                            timestamp = textEvent.createdAt,
                            textContent = textEvent.content,
                            imageLinks = textEvent.content.urlsInText()
                        )
                    }

                Log.i(APP_TAG,"intermediatePostsCacheSize: ${posts.size}")

                Log.i(APP_TAG,"postsCacheSize: ${postCache.size}")

                val feed = posts
                    //.filter { it.postId.isNotBlank() }
                    .distinctBy { it.postId }
                    .sortedByDescending { it.timestamp }
//                feed.forEach { post ->
//                    postCache.add(post)
//                }
                Log.i(APP_TAG,"feedPostsSize: ${feed.size}")
                if (feed.isEmpty()){
                    _feedContent.update { FeedState.Empty }
                    cancel()
                } else {
                    _feedContent.update { FeedState.Loaded(feed) }
                    cancel()
                }

            }


        }

    }
    fun refresh(){
        eventsCache.clear()
        profiles.clear()
        postCache.clear()
        getUpdateFeed()
    }

    override fun onCleared() {
        Log.i(APP_TAG, "FeedVM cleared.")
        viewModelScope.cancel()
        super.onCleared()
    }
    fun clean(){
        onCleared()
    }



    //TODO: Make it work!
//    fun getProfiles(): List<Profile> {
//        return emptyList()
//    }
}

val opsList = listOf(
    Post(
        user = User(
            username = "Satoshi Nakamoto 1",
            pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        ),
        textContent = "One of the user's very very long messages.",
        quotedPost = Post()
    ),
    Post(
        user = User(
            username = "Satoshi Nakamoto 2",
            pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        ),
        textContent = "One of the user's very very long messages."
    ),
    Post(
        user = User(
            username = "Another Profile",
            pubKey = "ghghqwertyuiopkey",
        ),
        textContent = "One of the user's very very long messages."
    ),
    Post(
        user = User(
            username = "Satoshi Nakamoto 3",
            pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
        ),
        textContent = "One of the user's very very long messages.",
        quotedPost = Post(
            user = User(
                username = "Satoshi Nakamoto 7",
                pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
            ),
            textContent = "One of the user's very very long messages."
        )
    ),
    Post(
        user = User(
            username = "Satoshi Nakamoto 4",
            pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
        ),
        textContent = "One of the user's very very long messages."
    ),
    Post(
        user = User(
            username = "Satoshi Nakamoto 5",
            pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
        ),
        textContent = "One of the user's very very long messages."
    ),
    Post(
        user = User(
            username = "Satoshi Nakamoto 6",
            pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
        ),
        textContent = "One of the user's very very long messages."
    ),
    Post(
        user = User(
            username = "Satoshi Nakamoto 7",
            pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
        ),
        textContent = "One of the user's very very long messages."
    ),
    Post(
        user = User(
            username = "Satoshi Nakamoto 8",
            pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
        ),
        textContent = "One of the user's very very long messages."
    ),
    Post(),
    Post(),
    Post(),
    Post(),
    Post(),
    Post(),
    Post()
)