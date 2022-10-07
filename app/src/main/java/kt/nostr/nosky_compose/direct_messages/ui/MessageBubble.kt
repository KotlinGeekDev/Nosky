package kt.nostr.nosky_compose.direct_messages.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kt.nostr.nosky_compose.direct_messages.Models.MessageItem
import kt.nostr.nosky_compose.reusable_components.GrayText


@Composable
fun MessageBubble(message: MessageItem) {
    Column(modifier = Modifier
        .fillMaxWidth()

        .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = when {
            message.isMine -> Alignment.End
            else -> Alignment.Start
        }) {
        Box(modifier = Modifier
            .widthIn(max = 340.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = when {
                    message.isMine -> MaterialTheme.colors.primary
                    else -> MaterialTheme.colors.secondary
                }
            )) {
            Text(
                text = message.message.text,
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                color = when {
                    message.isMine -> MaterialTheme.colors.onPrimary
                    else -> MaterialTheme.colors.onSecondary },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        GrayText(text = "Sent at 11:30 PM.")
    }
}