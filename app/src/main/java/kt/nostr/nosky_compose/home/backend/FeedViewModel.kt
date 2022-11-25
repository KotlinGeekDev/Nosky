package kt.nostr.nosky_compose.home.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(): ViewModel() {
    private val _feedContent = MutableStateFlow<List<Post>>(emptyList())
    val feedContent = _feedContent.asStateFlow()

//    init {
//        getUpdateFeed()
//    }

    //TODO : Replace with a call to NostrService.
    fun getUpdateFeed(){
        viewModelScope.launch {
            delay(5000)
            val feed = opsList
            _feedContent.update { it + feed }
        }


    }

    //TODO: Make it work!
//    fun getProfiles(): List<Profile> {
//        return emptyList()
//    }
}

val opsList = listOf(
    Post(
        username = "Satoshi Nakamoto 1",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContent = "One of the user's very very long messages.",
        quotedPost = Post()
    ),
    Post(
        username = "Satoshi Nakamoto 2",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContent = "One of the user's very very long messages."
    ),
    Post(
        username = "Satoshi Nakamoto 3",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContent = "One of the user's very very long messages.",
        quotedPost = Post(
            username = "Satoshi Nakamoto 7",
            userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
            textContent = "One of the user's very very long messages."
        )
    ),
    Post(
        username = "Satoshi Nakamoto 4",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContent = "One of the user's very very long messages."
    ),
    Post(
        username = "Satoshi Nakamoto 5",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContent = "One of the user's very very long messages."
    ),
    Post(
        username = "Satoshi Nakamoto 6",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContent = "One of the user's very very long messages."
    ),
    Post(
        username = "Satoshi Nakamoto 7",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
        textContent = "One of the user's very very long messages."
    ),
    Post(
        username = "Satoshi Nakamoto 8",
        userKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
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