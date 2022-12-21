package kt.nostr.nosky_compose.home.backend

import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kt.nostr.nosky_compose.main_backend.NostrService
import kt.nostr.nosky_compose.utility_functions.APP_TAG
import kt.nostr.nosky_compose.utility_functions.misc.currentSystemUnixTimeStamp
import kt.nostr.nosky_compose.utility_functions.misc.toHexString
import kt.nostr.nosky_compose.utility_functions.urlsInText
import ktnostr.currentTimestampFromInstant
import nostr.postr.events.MetadataEvent
import nostr.postr.events.TextNoteEvent
import nostr.postr.toHex
import kotlin.random.Random

@Stable
@Parcelize
data class Post(
    val postId: String = "",
    val timestamp: Long = currentSystemUnixTimeStamp(),
    val user: User = User(),
    val textContent: String = "",
    val imageLinks: List<String> = emptyList(),
    val quotedPost: Post? = null,
    val replies: List<Post> = emptyList()
    ): Parcelable

@Stable
@Parcelize
data class User(
    val username: String = " ",
    val pubKey: String = Random.nextBytes(32).toHexString(),
    val bio: String = "",
    val image: String = ""
    ): Parcelable

class PostViewModel(): ViewModel() {



    val nostrService = NostrService
    private var repliesFetcherJob: Job? = null
    private val replyTextCache: MutableList<TextNoteEvent> = mutableListOf()
    private val replyProfiles: MutableList<MetadataEvent> = mutableListOf()
    private val repliesCache: MutableSet<Post> = mutableSetOf()

    private val _internalPost = MutableStateFlow(Post())
    val postContent = _internalPost.asStateFlow()

    private val _repliesUiState: MutableStateFlow<RepliesUiState> =
        MutableStateFlow(RepliesUiState.Loading)
    val repliesUiState = _repliesUiState.asStateFlow()
        //.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), RepliesUiState.Loading)
        init {
            Log.i(APP_TAG, "repliesCache size: ${repliesCache.size}")
        }

    fun sendPost(privKey: ByteArray){
        val actualPost = postContent.value
        println("Old post timestamp: ${actualPost.timestamp}")
        _internalPost.update {
            it.copy(timestamp = currentTimestampFromInstant())
        }
        println("New post timestamp: ${actualPost.timestamp}")
        nostrService.sendPost(actualPost.textContent, actualPost.timestamp, privKey)
    }

    fun sendReply(privKey: ByteArray, reply: String, rootEventId: String){
        nostrService.sendPost(reply, currentTimestampFromInstant(), privKey, listOf(rootEventId))
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
        if (_repliesUiState.value != RepliesUiState.Loading){
            _repliesUiState.update { RepliesUiState.Loading }
        }
        repliesFetcherJob = viewModelScope.launch {
            try {
                nostrService.getTextEvents(eventId = postId)
                    .catch { println("Replies error. ${it.message}") }
                    .collect {
                        replyTextCache.add(it)
                    }


                if (replyTextCache.isEmpty()) _repliesUiState.update {
                    RepliesUiState.Loaded(emptyList())
                }
                val replyKeys = replyTextCache.map { it.pubKey.toHex() }.distinct()
                val currentTime = currentSystemUnixTimeStamp()
                nostrService
                    .getProfilesInfo(replyKeys)
                    .onEach { Log.i(APP_TAG,"Obtained profile: Name ->${it.contactMetaData.name} Image->${it.contactMetaData.picture}") }
                    .onCompletion { Log.i(APP_TAG,"Obtained reply profiles") }
                    .catch { error ->
                        _repliesUiState.update {
                            RepliesUiState.LoadingError(Exception(error.message))
                        }
                    }
                    .collect {
                        if (currentSystemUnixTimeStamp() - currentTime == 10L){
                            repliesFetcherJob?.cancel("Replies took too long to load.")
                        }
                        else {
                            replyProfiles.add(it)
                        }

                    }

                val eventsByPubkey = replyTextCache
                    .distinctBy { it.id.toHex() }
                    .associateBy { it.pubKey.toHex() }

                Log.i(APP_TAG,"eventsByPubkey Pubkey Size: ${eventsByPubkey.keys.size}")
                Log.i(APP_TAG,"eventsByPubkey Event Size: ${eventsByPubkey.values.size}")

                val posts = replyProfiles
                    //.filter { eventsByPubkey[it.pubKey.toHex()] != null }
                    .map { metadataEvent ->
                        val textEvent = eventsByPubkey[metadataEvent.pubKey.toHex()]
                        if (textEvent != null){
                            val post = Post(
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
                            repliesCache.add(post)
                        }
                    }
                Log.i(APP_TAG, "Event mapping executions: ${posts.size}")
                Log.i(APP_TAG, "repliesCache size: ${repliesCache.size}")
                _repliesUiState.update { RepliesUiState.Loaded(repliesCache.toList()) }
                cancel()
            } catch (e: Exception) {
                delay(2000)
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
    fun dispose(){
        repliesFetcherJob?.cancel()
        replyTextCache.clear()
        replyProfiles.clear()
        repliesCache.clear()
        repliesFetcherJob = null
        onCleared()
    }

}

