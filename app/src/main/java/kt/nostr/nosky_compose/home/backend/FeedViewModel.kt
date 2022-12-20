package kt.nostr.nosky_compose.home.backend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
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

    private val nostrService = NostrService.get()

    private val eventsCache: MutableSet<TextNoteEvent> = mutableSetOf()
    private val profiles: MutableSet<MetadataEvent> = mutableSetOf()

    private val _feedContent = MutableStateFlow<FeedState>(FeedState.Loading)
    val feedContent = _feedContent.asStateFlow()

    private val postCache: MutableList<Post> = mutableListOf()

//    init {
//        getUpdateFeed()
//    }

    //TODO : Replace with a call to NostrService.
    fun getUpdateFeed(){


        viewModelScope.launch {
            if (_feedContent.value != FeedState.Loading){
                _feedContent.update {
                    FeedState.Loading
                }
            }

            if (postCache.isNotEmpty()){
                Log.i(APP_TAG, "Taking cached events instead. Cache size: ${postCache.size}")
                //val feed = postCache.distinctBy { it.postId }.sortedByDescending { it.timestamp }
                //_feedContent.update { postCache.toList() }
            } else {

                nostrService.getTextEvents()

                    .onCompletion { Log.i(APP_TAG,"Text events completed.") }
                    .distinctUntilChanged { old, new ->
                        old.id.contentEquals(new.id)
                    }
                    .catch { e ->
                        Log.i(APP_TAG,"Feed flow. Caught ${e.message}")
                        _feedContent.update { FeedState.FeedError(e.message?: "Could not load feed.") }
                    }
                    .collect {
                        eventsCache.add(it)
                        Log.i(APP_TAG,"Collected event ${it.content} ")
                    }

                Log.i(APP_TAG,"eventsCacheSize: ${eventsCache.size}")
                delay(2000)
                val pubkeys = eventsCache.map { it.pubKey.toHex() }.distinct()
                Log.i(APP_TAG,"pubkeys_Size: ${pubkeys.size}")
                nostrService.getProfilesInfo(pubkeys)
                    .onCompletion { Log.i(APP_TAG,"Profiles completed.") }
                    .catch { Log.e(APP_TAG,"Profiles flow. Caught ${it.message}") }

                    .collect {
                        profiles.add(it)
                        Log.i(APP_TAG,"Obtained profile: Name ->${it.contactMetaData.name} Image->${it.contactMetaData.picture}")
                    }
                Log.i(APP_TAG,"profilesCacheSize: ${profiles.size}")

                val eventsByPubkey = eventsCache
                    .distinctBy { it.id.toHex() }
                    .associateBy { it.pubKey.toHex() }

                Log.i(APP_TAG,"eventsByPubkey Pubkey Size: ${eventsByPubkey.keys.size}")
                Log.i(APP_TAG,"eventsByPubkey Event Size: ${eventsByPubkey.values.size}")
                val posts = profiles
                    //.filter { eventsByPubkey[it.pubKey.toHex()] != null }
                    .map { metadataEvent ->
                        val textEvent = eventsByPubkey[metadataEvent.pubKey.toHex()]
                        if (textEvent != null){
                            if (textEvent.pubKey.toHex() == metadataEvent.pubKey.toHex()){
                                val post = Post(
                                    user = User(
                                        username = metadataEvent
                                            .contactMetaData.name ?: textEvent.pubKey.toNpub().take(9),
                                        pubKey = textEvent.pubKey.toHex(),
                                        bio = metadataEvent.contactMetaData.about ?: "",
                                        image = metadataEvent.contactMetaData.picture ?: ""
                                    ),
                                    postId = textEvent.id.toHex(),
                                    timestamp = textEvent.createdAt,
                                    textContent = textEvent.content,
                                    imageLinks = textEvent.content.urlsInText()
                                )
                                postCache.add(post)
                            } else {
                                val post = Post(
                                    user = User(
                                        username = textEvent.pubKey.toNpub().take(9),
                                        pubKey = textEvent.pubKey.toHex(),
                                        bio = metadataEvent.contactMetaData.about ?: "",
                                        image = metadataEvent.contactMetaData.picture ?: ""
                                    ),
                                    postId = textEvent.id.toHex(),
                                    timestamp = textEvent.createdAt,
                                    textContent = textEvent.content,
                                    imageLinks = textEvent.content.urlsInText()
                                )
                                postCache.add(post)
                            }

                        }
                    }
                Log.i(APP_TAG,"intermediatePostsCacheSize: ${posts.size}")
//            val feed = eventsCache.zip(profiles){ textEvent, profile ->
//                if (textEvent.pubKey.toHex() == profile.pubKey.toHex()){
//                    val metadata = profile.contactMetaData
//                    Post(
//                        user = User(
//                            username = metadata.name,
//                            pubKey = profile.pubKey.toHex(),
//                            image = metadata.picture ?: ""),
//                        postId = textEvent.id.toHex(),
//                        timestamp = textEvent.createdAt,
//                        textContent = textEvent.content,
//                        imageLinks = textEvent.content.urlsInText()
//                    )
//                } else {
//                    Post(
//                        user = User(
//                            username = profile.contactMetaData.name,
//                            pubKey = textEvent.pubKey.toHex(),
//                            image = profile.contactMetaData.picture ?: ""),
//                        postId = textEvent.id.toHex(),
//                        timestamp = textEvent.createdAt,
//                        textContent = textEvent.content,
//                        imageLinks = textEvent.content.urlsInText()
//                    )
//                }
//            }.distinctBy { it.postId }.sortedByDescending { it.timestamp }
                Log.i(APP_TAG,"postsCacheSize: ${postCache.size}")

                val feed = postCache
                    //.filter { it.postId.isNotBlank() }
                    //.distinctBy { it.postId }
                    .sortedByDescending { it.timestamp }
                feed.forEach { post ->
                    postCache.add(post)
                }
                Log.i(APP_TAG,"feedPostsSize: ${feed.size}")
                if (feed.isEmpty()){
                    _feedContent.update { FeedState.Empty }
                } else {
                    _feedContent.update { FeedState.Loaded(feed) }
                }

            }
        }

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