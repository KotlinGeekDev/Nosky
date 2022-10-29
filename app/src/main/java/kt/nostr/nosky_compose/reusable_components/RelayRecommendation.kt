package kt.nostr.nosky_compose.reusable_components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun RelayRecommendation() {
    Snackbar(
        modifier = Modifier.padding(4.dp),
        action = {
        TextButton(onClick = {  }) {
            Text(text = "Add")
        }
    }) {
        Text(text = "New relay found.")
    }
}

@Composable
fun CustomRelayRecommendation() {
    Card(
        elevation = 6.dp,
        modifier = Modifier.height(45.dp).fillMaxWidth(),
        contentColor = MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.small,
        backgroundColor = SnackbarDefaults.backgroundColor
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "New relay has been recommended. Tap to add.",
                modifier = Modifier
                    .weight(3f)
                    .padding(3.dp)
                    .alignBy { it.measuredHeight / 2 })
            Button(
                onClick = { /*TODO*/ },
                Modifier
                    .weight(1f, fill = false)
                    .padding(end = 5.dp)
            ) {
                Text(
                    text = "Add",
                    lineHeight = 4.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}