package kt.nostr.nosky_compose.home.backend

import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kt.nostr.nosky_compose.main_backend.NostrService
import kt.nostr.nosky_compose.utility_functions.APP_TAG
import kt.nostr.nosky_compose.utility_functions.isURL
import kt.nostr.nosky_compose.utility_functions.misc.currentSystemUnixTimeStamp
import kt.nostr.nosky_compose.utility_functions.misc.toHexString
import kt.nostr.nosky_compose.utility_functions.urlsInText
import ktnostr.currentTimestampFromInstant
import nostr.postr.toHex
import kotlin.random.Random

@Stable
@Parcelize
data class Post(
    val postId: String = "",
    val timestamp: Long = currentSystemUnixTimeStamp(),
    val user: User = User(),
    val textContent: String = "Nostr post content",
    val imageLinks: List<String> = emptyList(),
    val quotedPost: Post? = null,
    val replies: List<Post> = emptyList()
    ): Parcelable

@Stable
@Parcelize
class User(
    val username: String = " ",
    val pubKey: String = Random.nextBytes(32).toHexString(),
    val bio: String = "",
    val image: String = ""
    ): Parcelable

class PostViewModel(): ViewModel() {

    val nostrService = NostrService.get()
    private val repliesCache: MutableList<Post> = mutableListOf()

    private val _internalPost = MutableStateFlow(Post())
    val postContent = _internalPost.asStateFlow()

    private val _repliesUiState: MutableStateFlow<RepliesUiState> =
        MutableStateFlow(RepliesUiState.Loading)
    val repliesUiState = _repliesUiState.asStateFlow()

    fun sendPost(privKey: ByteArray){
        val actualPost = postContent.value
        println("Old post timestamp: ${actualPost.timestamp}")
        _internalPost.update {
            it.copy(timestamp = currentTimestampFromInstant())
        }
        println("New post timestamp: ${actualPost.timestamp}")
        nostrService.sendPost(actualPost.textContent, actualPost.timestamp, privKey)
    }

    fun updateTextContent(text: String){
        _internalPost.update { it.copy(textContent = text) }
    }

    fun addImageLink(newImageLink: String){
        _internalPost.update { it.copy(imageLinks = it.imageLinks + newImageLink) }
    }

    fun quotePost(postToQuote: Post){
        _internalPost.update {
            it.copy(quotedPost = postToQuote)
        }
    }

    fun getRepliesForPost(postId: String){
//        if (_repliesUiState.value != RepliesUiState.Loading){
//            _repliesUiState.update { RepliesUiState.Loading }
//        }
        viewModelScope.launch {
            try {
                val replyEvents = nostrService.getReplies(postId)
                val associatedProfiles = nostrService
                    .getProfilesInfo(replyEvents.map { it.pubKey.toHex() })
                    .onEach { Log.i(APP_TAG,"Obtained profile: Name ->${it.contactMetaData.name} Image->${it.contactMetaData.picture}") }
                    .onCompletion { Log.i(APP_TAG,"Obtained reply profiles") }
                    .toList()

                val eventsByPubkey = replyEvents.associateBy { it.pubKey.toHex() }

                val posts = associatedProfiles
                    //.filter { eventsByPubkey[it.pubKey.toHex()] != null }
                    .map { metadataEvent ->
                        val textEvent = eventsByPubkey[metadataEvent.pubKey.toHex()]
                        if (textEvent != null){
                            Post(
                                user = User(
                                    username = metadataEvent.contactMetaData.name ?: " ",
                                    pubKey = textEvent.pubKey.toHex(),
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
                posts.filter { it.postId.isNotBlank() }.forEach { post ->
                    repliesCache.add(post)
                }

                _repliesUiState.update { RepliesUiState.Loaded(repliesCache) }
            } catch (e: Exception) {
                _repliesUiState.update { RepliesUiState.LoadingError(e) }
            }

        }

    }

    override fun onCleared() {
        _internalPost.update {
            it.copy()
        }
        Log.i(APP_TAG,"PostViewModel cleared.")
        super.onCleared()
    }

    fun textIsLink(): Boolean = postContent.value.textContent.isURL()
}

