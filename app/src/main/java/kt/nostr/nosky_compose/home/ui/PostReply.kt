package kt.nostr.nosky_compose.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.reusable_ui_components.QuotedPost
import kt.nostr.nosky_compose.reusable_ui_components.theme.NoskycomposeTheme

@Composable
fun PostReply(
    originalPost: Post = Post(
        textContent = "One of the user's very very long messages. " +
                "from 8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f " +
                "about working something blah blah"
    ),
    closeDialog: () -> Unit,
    postReply: () -> Unit) {

    var replyContent by remember {
        mutableStateOf("")
    }


    AlertDialog(
        onDismissRequest = { closeDialog() },
        confirmButton = {
              OutlinedButton(
                  onClick = { postReply();closeDialog() },
                  enabled = replyContent.isNotBlank()
              ) {
                  Text(text = "Reply")
              }
        },
        modifier = Modifier
            .fillMaxWidth(),
            //.fillMaxHeight(0.9f),
//        dismissButton = {
//              OutlinedButton(onClick = { closeDialog() }) {
//                  Text(text = "Cancel")
//              }
//        },
        title = {

             QuotedPost(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(bottom = 10.dp),
                 userName = originalPost.username,
                 userPubkey = originalPost.userKey,
                 post = originalPost.textContent,
                 onPostClick = {})
            Spacer(modifier = Modifier.height(10.dp))
        },
        text = {

            CustomDivider()
            OutlinedTextField(
                value = replyContent,
                onValueChange = { replyContent = it },
                modifier = Modifier
                    //.fillMaxHeight(0.8f)
                    //.padding(top = 20.dp)
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
        })



}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ReplyUIPreview() {
    NoskycomposeTheme {
        PostReply(closeDialog = { }) {}
    }
}