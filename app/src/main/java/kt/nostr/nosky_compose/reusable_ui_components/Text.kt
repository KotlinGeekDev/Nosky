package kt.nostr.nosky_compose.reusable_ui_components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun ThemedText(modifier: Modifier = Modifier, text: String,
               style: TextStyle = TextStyle(),
               maxLines: Int = Int.MAX_VALUE,
               textColor: Color = MaterialTheme.colors.onSurface,
               textOverflow: TextOverflow = TextOverflow.Ellipsis) {
    Text(
        text = text,
        style = style,
        modifier = modifier,
        color = textColor,
        maxLines = maxLines,
        overflow = textOverflow
    )
}


@Composable
fun GrayText(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontSize = 14.sp,
            color = if (isSystemInDarkTheme()) Color(0xFF666666) else Color.Gray
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}