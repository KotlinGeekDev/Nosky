@file:OptIn(ExperimentalMaterialApi::class)

package kt.nostr.nosky_compose.settings.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kt.nostr.nosky_compose.reusable_ui_components.AppTopBar
import kt.nostr.nosky_compose.reusable_ui_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.utility_functions.misc.LinkInfo

@Composable
fun AppInformationDetails(goBackToMainSettings: () -> Unit) {

    BackHandler {
        goBackToMainSettings()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                label = "About Nosky",
                goBack = { goBackToMainSettings() })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Nosky is a native Android client for Nostr, a social network for the user.",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(CenterHorizontally),
                fontWeight = FontWeight.W400
            )
            Spacer(modifier = Modifier.height(10.dp))
            LinkInfo.values().forEach { linkInfo ->
                LinkElement(label = linkInfo.description, link = linkInfo.link)
                Spacer(modifier = Modifier.height(10.dp))
            }

        }
    }

}

@Composable
fun LinkElement(label: String, link: String) {

    val uriHandler = LocalUriHandler.current

    Card(
        onClick = { uriHandler.openUri(link) },
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .height(60.dp)
            .fillMaxWidth(),
        elevation = 6.dp,
        contentColor = MaterialTheme.colors.onSurface,
        shape = MaterialTheme.shapes.small,
        backgroundColor = MaterialTheme.colors.onSurface
            .copy(alpha = 0.1f)
            .compositeOver(MaterialTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun AppInfoPreview() {
    NoskycomposeTheme {
        AppInformationDetails {

        }
    }
}