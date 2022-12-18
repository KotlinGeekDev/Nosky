package kt.nostr.nosky_compose.home.backend

sealed class RepliesUiState {
    object Loading: RepliesUiState()
    class Loaded(val replies: List<Post>): RepliesUiState()
    class LoadingError(val error: Exception): RepliesUiState()
}