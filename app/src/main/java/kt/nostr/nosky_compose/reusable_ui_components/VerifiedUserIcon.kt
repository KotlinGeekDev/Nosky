package kt.nostr.nosky_compose.reusable_ui_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp

@Composable
fun VerifiedUserIcon(modifier: Modifier = Modifier) {

    Image(
        imageVector = Icons.Default.Verified,
        contentDescription = "",
        modifier = Modifier.requiredSize(18.dp).then(modifier),
        alignment = Alignment.Center,
        colorFilter = ColorFilter.tint(LocalContentColor.current.copy(alpha = LocalContentAlpha.current))
    )
}