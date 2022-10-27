package kt.nostr.nosky_compose.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kt.nostr.nosky_compose.reusable_components.ThemedText

@Composable
fun TestPopupScreen(onExit: () -> Unit) {
    var postContent by remember {
        mutableStateOf("")
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
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            dismissButton = {
                OutlinedButton(onClick = { onExit() }) {
                    Text(text = "Cancel")
                }
            },
            title = {
                ThemedText(
                    text = "New Post",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    maxLines = 1,
                    textColor = MaterialTheme.colors.onSurface
                )
            },
            text = {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = postContent,
                    onValueChange = { postContent = it },
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxSize()
                    //.padding(horizontal = 2.dp, vertical = 4.dp)
                )
            },
            properties = dialogProperties
        )
    }
}