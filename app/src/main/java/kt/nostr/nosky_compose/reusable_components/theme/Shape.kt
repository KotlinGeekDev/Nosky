package kt.nostr.nosky_compose.reusable_components.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

val BottomSheetShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
    large = RoundedCornerShape(0.dp)
)