package kt.nostr.nosky_compose.direct_messages.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import kt.nostr.nosky_compose.direct_messages.Models.MessageItem
import kt.nostr.nosky_compose.direct_messages.Models.messageList

class MessageListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(activity?.application!!.applicationContext)
        composeView.setContent {
            LazyColumn() {
                items(messageList){ item: MessageItem ->
                    MessageBubble(message = item)
                }
            }
        }
        return composeView
    }
}