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


class FeedViewModel(): ViewModel() {

    private val nostrService = NostrService.get()

    private val eventsCache: MutableList<TextNoteEvent> = mutableListOf()
    private val profiles: MutableList<MetadataEvent> = mutableListOf()

    private val _feedContent = MutableStateFlow<List<Post>>(emptyList())
    val feedContent = _feedContent.asStateFlow()

    private val postCache: MutableList<Post> = mutableListOf()

//    init {
//        getUpdateFeed()
//    }

    //TODO : Replace with a call to NostrService.
    fun getUpdateFeed(){

        viewModelScope.launch {
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
                    .catch { Log.i(APP_TAG,"Feed flow. Caught ${it.message}") }
                    .collect {
                        eventsCache.add(it)
                        Log.i(APP_TAG,"Collected event ${it.content} ")
                    }

                Log.i(APP_TAG,"eventsCacheSize: ${eventsCache.size}")
                delay(2000)
                val pubkeys = eventsCache.map { it.pubKey.toHex() }.distinct()
                Log.i(APP_TAG,"pubkeys_Size: ${pubkeys.size}")
                nostrService.getProfilesInfo(
                    pubkeys
                )

                    .onCompletion { Log.i(APP_TAG,"Profiles completed.") }
                    .distinctUntilChanged { old, new ->
                        old.id.contentEquals(new.id)
                    }.catch { Log.e(APP_TAG,"Profiles flow. Caught ${it.message}") }

                    .collect {
                        profiles.add(it)
                        Log.i(APP_TAG,"Obtained profile: Name ->${it.contactMetaData.name} Image->${it.contactMetaData.picture}")
                    }
                Log.i(APP_TAG,"profilesCacheSize: ${profiles.size}")

                val eventsByPubkey = eventsCache.associateBy { it.pubKey.toHex() }

                Log.i(APP_TAG,"eventsByPubkeySize: ${eventsByPubkey.size}")
                val posts = profiles
                    //.filter { eventsByPubkey[it.pubKey.toHex()] != null }
                    .map { metadataEvent ->
                        val textEvent = eventsByPubkey[metadataEvent.pubKey.toHex()]
                        if (textEvent != null){
                            Post(
                                user = User(
                                    username = metadataEvent
                                        .contactMetaData.name ?: textEvent.pubKey.toNpub().take(9),
                                    pubKey = textEvent.pubKey.toNpub(),
                                    bio = metadataEvent.contactMetaData.about ?: "",
                                    image = metadataEvent.contactMetaData.picture ?: ""
                                ),
                                postId = textEvent.id.toHex(),
                                timestamp = textEvent.createdAt,
                                textContent = textEvent.content,
                                imageLinks = textEvent.content.urlsInText()
                            )
                        } else {
                            Log.i(APP_TAG, "Profile ${metadataEvent.contactMetaData.name} has no posts.")
                            Post()
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


                val feed = posts
                    //.filter { it.postId.isNotBlank() }
                    .distinctBy { it.postId }
                    .sortedByDescending { it.timestamp }
                feed.forEach { post ->
                    postCache.add(post)
                }
                Log.i(APP_TAG,"feedPostsSize: ${feed.size}")
                _feedContent.update { it + feed }
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