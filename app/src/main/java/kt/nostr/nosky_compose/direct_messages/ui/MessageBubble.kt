package kt.nostr.nosky_compose.direct_messages.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kt.nostr.nosky_compose.direct_messages.Models.MessageItem
import kt.nostr.nosky_compose.reusable_ui_components.GrayText
import kt.nostr.nosky_compose.reusable_ui_components.LinkifyText
import ktnostr.currentUnixTimeStampFromInstant
import ktnostr.formattedDateTime


@Composable
fun MessageBubble(message: MessageItem) {
    val timeStamp = remember {
        formattedDateTime(currentUnixTimeStampFromInstant() - (60 * 60 *24*2))
    }
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
                    message.isMine -> MaterialTheme.colors.primary.copy(alpha = 0.65f)
                    else -> Color.DarkGray.copy(alpha = 0.4f)
                }
            )) {

            LinkifyText(
                text = message.message.text,
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        GrayText(text = timeStamp)
    }
}