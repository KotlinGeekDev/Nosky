package kt.nostr.nosky_compose.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.home.backend.PostViewModel
import kt.nostr.nosky_compose.profile.LoggedInProfileProvider
import ktnostr.crypto.toBytes


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
private fun PostButton(postText: String, onPost: () -> Unit = {}) {
    Button(
        onClick = {
            onPost()
        },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults
            .buttonColors(
                backgroundColor = if (postText.isEmpty())
                    Color(0xFFAAAAAA) else MaterialTheme.colors.primary
            )
    ) {
        Text(text = "Post", color = Color.White)
    }
}

@Composable
fun NewPostView(onClose: () -> Unit) {

    val localProfile = LoggedInProfileProvider.getLoggedProfile(LocalContext.current)

    val postViewModel: PostViewModel = viewModel()
    val post by postViewModel.postContent.collectAsState()



    val dialogProperties = DialogProperties()
    Dialog(
        onDismissRequest = { onClose() }, properties = dialogProperties
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CloseButton(onCancel = onClose)

                    PostButton(
                        post.textContent,
                        onPost = {
                            postViewModel.sendPost(localProfile.privKey.toBytes())
                            onClose()
                        }
                    )
                }

                OutlinedTextField(
                    value = post.textContent,
                    onValueChange = postViewModel::updateTextContent,
                    modifier = Modifier
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
            }

        }
    }
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun AlternativeLayoutPreview() {
    NoskycomposeTheme {
        NewPostView {
            
        }
    }
}