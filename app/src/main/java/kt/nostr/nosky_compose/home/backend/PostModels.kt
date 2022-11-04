package kt.nostr.nosky_compose.home.backend

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import kt.nostr.nosky_compose.utility_functions.isURL
import ktnostr.crypto.toHexString
import kotlin.random.Random

@Stable
@Parcelize
data class Post(
    val username: String = "Nostr Username",
    val userKey: String = Random.nextBytes(32).toHexString(),
    val textContent: String = "Nostr post content",
    val imageLinks: List<String> = emptyList(),
    val quotedPost: Post? = null,
    val replies: List<Post> = emptyList()
    ): Parcelable

class PostViewModel(): ViewModel() {


    private val _internalPost = MutableStateFlow(Post())
    val postContent = _internalPost.asStateFlow()


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



    fun urlsInText(): List<String> {
        val listSubstrings = postContent.value.textContent.split(" ")
        val links = listSubstrings.filter { substring -> substring.isURL() }
        return links.filter {
            it.endsWith(".jpg")
                    || it.endsWith(".img")
                    || it.endsWith(".webp")
        }
    }

    fun textIsLink(): Boolean = postContent.value.textContent.isURL()
}

