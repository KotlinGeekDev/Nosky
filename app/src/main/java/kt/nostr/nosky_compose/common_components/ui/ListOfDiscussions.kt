package kt.nostr.nosky_compose.common_components.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.sharp.PersonOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun ListOfDiscussions() {

}

@Composable
fun DiscussionItem(modifier: Modifier = Modifier,
                   user: String,
                   text: String,
                   onItemClick: () -> Unit,
                   onAvatarClick: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onItemClick() }
//            .combinedClickable(
//                onClick = onItemClick,
//            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ImageAvatar(
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        end = 4.dp,
                        top = 12.dp,
                        bottom = 12.dp
                    )
                    .size(40.dp),
                image = rememberVectorPainter(Icons.Filled.Person)
            )

            Column(
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp)
                    .weight(1f)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                val channelName: (@Composable (modifier: Modifier) -> Unit) = @Composable {
                    Text(
                        modifier = it,
                        text = user,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            fontFamily = null
                        ),
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (1 == Random.nextInt(0, 3)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        channelName(Modifier.weight(weight = 1f, fill = false))

                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(16.dp),
                            imageVector = Icons.Sharp.PersonOutline,
                            contentDescription = null
                        )
                    }
                } else {
                    channelName(Modifier)
                }

                if (text.isNotEmpty()) {
                    Text(
                        text = text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400,
                            fontFamily = null
                        )
                    )
                }
            }

            //trailingContent(channelItem)
        }
    }
}

@Composable
public fun ImageAvatar(
    image: Painter,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val clickableModifier: Modifier = if (onClick != null) {
        modifier.clickable(
            onClick = onClick,
            indication = rememberRipple(bounded = false),
            interactionSource = remember { MutableInteractionSource() }
        )
    } else {
        modifier
    }

    Image(
        modifier = clickableModifier.clip(shape),
        contentScale = ContentScale.Crop,
        painter = image,
        contentDescription = contentDescription
    )
}

@Composable
public fun InitialsAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    textStyle: TextStyle = TextStyle(
        fontSize = 18.sp,
        lineHeight = 25.sp,
        fontWeight = FontWeight.W500,
        fontFamily = null
    ),
    avatarOffset: DpOffset = DpOffset(0.dp, 0.dp),
    onClick: (() -> Unit)? = null,
) {
    val clickableModifier: Modifier = if (onClick != null) {
        modifier.clickable(
            onClick = onClick,
            indication = rememberRipple(bounded = false),
            interactionSource = remember { MutableInteractionSource() }
        )
    } else {
        modifier
    }


    Box(
        modifier = clickableModifier
            .clip(shape)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(avatarOffset.x, avatarOffset.y),
            text = initials,
            style = textStyle,
            color = Color.White
        )
    }
}