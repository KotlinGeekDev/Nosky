package kt.nostr.nosky_compose.direct_messages.Models

import androidx.compose.runtime.Stable

class MessageItem(val isMine: Boolean, val message: Message)

@Stable
data class Message(val user: Person, val text: String)

val messageList = List(5){
    val person = if (it.mod(2) == 0) Person("Me") else Person("Other")
    MessageItem(
        it.mod(2) == 0,
        Message(person, "Hi ${person.name}")
    )
}