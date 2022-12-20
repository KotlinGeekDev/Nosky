package kt.nostr.nosky_compose.profile.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.home.backend.User
import kt.nostr.nosky_compose.main_backend.NostrService
import kt.nostr.nosky_compose.utility_functions.APP_TAG
import kt.nostr.nosky_compose.utility_functions.urlsInText
import nostr.postr.events.MetadataEvent
import nostr.postr.events.TextNoteEvent
import nostr.postr.toHex

sealed class PostsResult {
    object Loading: PostsResult()
    data class Success(val postList: List<Post>): PostsResult()
    class Error(val obtainedError: Exception): PostsResult()
}

class ExternalProfileViewModel(): ViewModel() {

    private val nostrService = NostrService
//    private val _obtainedProfile = MutableStateFlow(Profile())
//    val obtainedProfile = _obtainedProfile.asStateFlow()

    private var postsFetcherJob: Job? = null
    private val _loaded = MutableStateFlow(false)
    val loaded = _loaded.asStateFlow()

    private val eventCache = mutableListOf<TextNoteEvent>()
    private val postsCache: MutableList<Post> = mutableListOf()

    private val otherPubkeyCache: MutableList<String> = mutableListOf()
    private val associatedCache = mutableListOf<MetadataEvent>()

    private val _postsFromProfile: MutableStateFlow<PostsResult> = MutableStateFlow(PostsResult.Loading)
    val postsFromProfile = _postsFromProfile.asStateFlow()

    fun getProfilePosts(profilePubkey: String) {
        if (postsCache.isEmpty()) {
            _postsFromProfile.update {
                PostsResult.Loading
            }
        }

        fun insertPosts() {
            eventCache.forEach { event ->
                if (event.pubKey.toHex() == profilePubkey) {
                    val post = Post(
                        user = User(pubKey = profilePubkey),
                        postId = event.id.toHex(),
                        timestamp = event.createdAt,
                        textContent = event.content,
                        imageLinks = event.content.urlsInText()
                    )
                    postsCache.add(post)
                    //eventCache.remove(event)
                }
            }
        }

        postsFetcherJob = viewModelScope.launch {

            nostrService.getTextEvents(listOf(profilePubkey))

                .onEach {
                    eventCache.add(it)
                }
                .onCompletion {
                    //this@launch.cancel()
                    Log.i(APP_TAG, "Finished obtaining posts for ${profilePubkey.take(5)}.")
                    //_postsFromProfile.update { PostsResult.Success() }
                }
                .catch { error ->
                    _postsFromProfile.update { PostsResult.Error(Exception(error.message)) }
                }
                .collect {
                    if (it.pubKey.toHex() != profilePubkey) {
                        otherPubkeyCache.add(it.pubKey.toHex())
                    }
                    if (!eventCache.contains(it) && it.pubKey.toHex() == profilePubkey) {
                        eventCache.add(it)
                    }
                }

            Log.i(APP_TAG, "Event cache size for ${profilePubkey.take(5)}: ${eventCache.size} ")


//            if (otherPubkeyCache.isNotEmpty()){
//                nostrService.getProfilesInfo(otherPubkeyCache.distinct())
//                    .onEach {
//                        associatedCache.add(it)
//                    }
//                    .onCompletion { Log.i(APP_TAG, "Finished collecting other profiles") }
//                    .collect {}
//                delay(1500)
//                Log.i(APP_TAG, "Associated cache size: ${associatedCache.size}")
//                if (associatedCache.isNotEmpty()){
//
//                    eventCache.forEachIndexed() { index, textNoteEvent ->
//                        val correctProfileIndex = associatedCache
//                            .indexOfFirst { textNoteEvent.pubKey.toHex() == it.pubKey.toHex() }
//                        val profile = associatedCache[correctProfileIndex]
//                        val post = Post(
//                            user = User(
//                                username = profile.contactMetaData.name,
//                                pubKey = textNoteEvent.pubKey.toHex(),
//                                image = profile.contactMetaData.picture),
//                            postId = textNoteEvent.id.toHex(),
//                            timestamp = textNoteEvent.createdAt,
//                            textContent = textNoteEvent.content,
//                            imageLinks = textNoteEvent.content.urlsInText()
//                        )
//                        postsCache.add(post)
//                    }
//                    val organizedPosts = postsCache
//                        .distinctBy { it.postId }
//                        .sortedByDescending { it.timestamp }
//
//                    _postsFromProfile.update { PostsResult.Success(organizedPosts) }
//                    _loaded.update { true }
//                    cancel()
//                } else{
//
//                }
            insertPosts()
            val organizedPosts = postsCache
                .distinctBy { it.postId }
                .sortedByDescending { it.timestamp }

            _postsFromProfile.update { PostsResult.Success(organizedPosts) }
            _loaded.update { true }
            cancel()

        }

    }

    override fun onCleared() {
        Log.i(APP_TAG, "ExternalProfileVM cleared.")

        super.onCleared()
    }

    fun clear(){

        eventCache.clear()
        postsCache.clear()
        postsFetcherJob?.cancel()
        onCleared()
    }

}