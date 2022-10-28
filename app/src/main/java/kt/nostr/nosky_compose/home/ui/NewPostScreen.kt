package kt.nostr.nosky_compose.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme

@Composable
fun TestPopupScreen(onExit: () -> Unit) {
    var postContent by remember {
        mutableStateOf(TextFieldValue(""))
    }

    val dialogProperties = DialogProperties()

    Surface(Modifier.fillMaxSize()) {
        AlertDialog(
            onDismissRequest = onExit,
            confirmButton = {
                OutlinedButton(onClick = { /*TODO*/ }) {
                    Text(text = "Post")
                }
            },
            modifier = Modifier
                .border(1.dp, color = Color.Transparent)
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            dismissButton = {
                OutlinedButton(onClick = { onExit() }) {
                    Text(text = "Cancel")
                }
            },
            title = {
//                ThemedText(
//                    text = "New Post",
//                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
//                    maxLines = 1,
//                    textColor = MaterialTheme.colors.onSurface
//                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CloseButton(onCancel = onExit)
                    Spacer(modifier = Modifier.width(20.dp))
                    TweetButton(postContent)
                }

            },
            text = {

                OutlinedTextField(
                    value = postContent,
                    onValueChange = { postContent = it },
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 20.dp)
                        .background(Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.surface,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    placeholder = {
                        Text(
                            text = "What's on your mind?",
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                            maxLines = 1
                        )
                                  },
                    colors = TextFieldDefaults
                        .outlinedTextFieldColors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        )
                )
                   },
            properties = dialogProperties
        )
    }
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun NewPostScreenPreview() {
    NoskycomposeTheme {
        TestPopupScreen {

        }
    }
}

@Composable
fun ComposeTweet(onCancel: () -> Unit) {
    var tweetText = remember {
        mutableStateOf(TextFieldValue(text = ""))
    }
    Surface(color = MaterialTheme.colors.surface) {
        Dialog(onDismissRequest = { onCancel() }) {
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colors.surface)
                    .fillMaxHeight(0.9f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CloseButton(onCancel = onCancel)
                    TweetButton(tweetText.value)
                }
                AvatarWithTextField(tweetText)
            }
        }
    }
}

@Composable
private fun AvatarWithTextField(tweetText: MutableState<TextFieldValue>) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp)
    ) {
        Image(
            painterResource(R.drawable.nosky_logo_modified),
            modifier = Modifier
                .clip(shape = RoundedCornerShape(17.dp))
                .border(1.dp, color = Color.Cyan, shape = CircleShape)
                .background(color = Color.Black)
                .size(34.dp),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(10.dp))
        TextFieldWithHint(
            modifier = Modifier.fillMaxWidth(),
            value = tweetText.value,
            onValueChange = { textFieldValue -> tweetText.value = textFieldValue },
            hint = "What's happening?"
        )
    }
}

@Composable
private fun CloseButton(onCancel: () -> Unit) {
    IconButton(onClick = { onCancel() }) {
        Icon(
            imageVector = Icons.Filled.Clear,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
private fun TweetButton(tweetText: TextFieldValue) {
    Button(
        onClick = {

        },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults
            .buttonColors(
                backgroundColor = if (tweetText.text.isEmpty())
                    Color(0xFFAAAAAA) else MaterialTheme.colors.primary
            )
    ) {
        Text(text = "Tweet", color = Color.White)
    }
}

@Composable
private fun TextFieldWithHint(
    value: TextFieldValue,
    modifier: Modifier,
    hint: String,
    onValueChange: (TextFieldValue) -> Unit
) {
    Row(
        //Modifier.weight(1f)
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        if (value.text.isEmpty()) Text(
            text = hint,
            style = TextStyle(color = Color(0xFF666666), fontSize = 18.sp)
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ComposeTweetPreview() {
    NoskycomposeTheme {
        ComposeTweet(onCancel = {})
    }
}