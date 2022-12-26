package kt.nostr.nosky_compose.settings.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.common_components.ui.AppTopBar
import kt.nostr.nosky_compose.settings.SettingsViewModel
import kt.nostr.nosky_compose.settings.backend.NostrRelay


//TODO: Feature: disable adding relay until it fits relay URL pattern.
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RelayManagementSettingsScreen(
    returnToMainPage: () -> Unit = {}
) {

    BackHandler {
        returnToMainPage()
    }

    val settingsContext = LocalContext.current
    val settingsViewModel = viewModel(
        initializer = {
            SettingsViewModel(settingsContext)
        }
    )

    val relayList by settingsViewModel.relays.collectAsState()

    val coroutineScope = rememberCoroutineScope()

//    var mutableRelayList by remember {
//
//        mutableStateOf(relayList)
//    }
    var isMenuExpanded by remember {
        mutableStateOf(false)
    }
    var areRelaysAddedCompletely by remember {
        mutableStateOf(false)
    }

    var wantsToAddRelay by remember {
        mutableStateOf(false)
    }

    if (wantsToAddRelay){
        AddRelayDialog(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            onAddRelay = { newRelay ->
                coroutineScope.launch {
                    if (newRelay.url.isBlank()){
                        areRelaysAddedCompletely = true
                        delay(3000)
                        areRelaysAddedCompletely = false
                        cancel()
                    } else {
                        settingsViewModel.addRelay(newRelay)
                        //mutableRelayList = mutableRelayList + newRelay
                    }
                }
            },
            onCancel = {
                wantsToAddRelay =!wantsToAddRelay
            }
        )
    }


    Scaffold(
     topBar = {
         AppTopBar(label = "Configure relays", goBack = returnToMainPage)
     },
     bottomBar = {
         AnimatedVisibility(
             visible = areRelaysAddedCompletely,
             enter = fadeIn() + expandVertically(),
             exit = fadeOut() + shrinkVertically()
         ) {
                 Snackbar(
                     modifier = Modifier
                         .padding(bottom = 40.dp, start = 4.dp, end = 4.dp),
                     action = {
                         TextButton(onClick = {
                             areRelaysAddedCompletely = !areRelaysAddedCompletely
                         }) {
                             Text(text = "OK")
                         }
                     }
                 ) {
                     Text(text = "Too many relays added.")
                 }

         }

     },
     floatingActionButton = {
         RelayScreenFab(
             modifier = Modifier.padding(bottom = 10.dp),
             isFabExpanded = isMenuExpanded,
             onFabExpandedOrCollapsed = { isMenuExpanded = !isMenuExpanded },
             onAddRelay = {

                 coroutineScope.launch {
                     if (isMenuExpanded) isMenuExpanded = false

                     wantsToAddRelay = true

                     cancel()
                 }

             },
             onBackupRelays = {
                 coroutineScope.launch {
                     isMenuExpanded = false
                     areRelaysAddedCompletely = true
                     delay(4000)
                     areRelaysAddedCompletely = false
                     cancel()
                 }
             },
             onResetRelayList = {
                 settingsViewModel.reset()
             }
         )
     }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            items(relayList){ relay ->
                RelayComponent(
                    modifier = Modifier.animateItemPlacement(),
                    relayUrl = relay.url,
                    readPolicy = relay.readPolicy,
                    writePolicy = relay.writePolicy,
                    onDelete = { settingsViewModel.removeRelay(relay) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }

        }



    }
}

@Composable
private fun RelayComponent(
    modifier: Modifier = Modifier,
    relayUrl: String = "",
    readPolicy: Boolean = true,
    writePolicy: Boolean = true,
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(horizontal = 5.dp)
            .height(70.dp)
            .fillMaxWidth(),
        elevation = 5.dp,
        shape = RoundedCornerShape(percent = 10),
        backgroundColor = MaterialTheme.colors.onSurface
                .copy(alpha = 0.1f)
                .compositeOver(MaterialTheme.colors.surface)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.padding(start = 5.dp),
                verticalAlignment = CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(8.dp)
                        .padding(top = 2.dp),
                    tint = Color.Green
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = relayUrl,
                    modifier = Modifier.weight(1f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete relay",
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(horizontal = 10.dp)
                        .size(25.dp)
                        .clickable { onDelete() }
                )
            }

        }

//        Row(
//            modifier = Modifier.padding(start = 20.dp, bottom = 2.dp),
//            verticalAlignment = Alignment.Bottom
//        ) {
//            Text(text = "Read-only", modifier = Modifier)
//        }

    }
}

@Composable
private fun AddRelayDialog(
    modifier: Modifier = Modifier,
    onAddRelay: (NostrRelay) -> Unit = { },
    onCancel:() -> Unit = {}
) {
    var relayToAdd by remember {
        mutableStateOf(NostrRelay("", readPolicy = true, writePolicy = true))
    }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
        title = {
            Text(text = "Add New Relay")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = relayToAdd.url,
                    onValueChange = {
                        relayToAdd = relayToAdd.copy(url = it)
                    }
                )
                Row {
                    LabeledCheckbox(
                        label = "Can read from",
                        checked = relayToAdd.readPolicy,
                        onCheckedChange = { newReadPolicy ->
                            relayToAdd = relayToAdd.copy(readPolicy = newReadPolicy)
                        })
                    Spacer(modifier = Modifier.width(5.dp))
                    LabeledCheckbox(
                        label = "Can publish to",
                        checked = relayToAdd.writePolicy,
                        onCheckedChange = { newWritePolicy ->
                            relayToAdd = relayToAdd.copy(writePolicy = newWritePolicy)
                        }
                    )
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = {
                onAddRelay(relayToAdd)
                onCancel()
            }) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text(text = "Cancel")
            }
        },

    )
}

@Composable
fun LabeledCheckbox(label: String,
                    checked: Boolean,
                    onCheckedChange: ((Boolean) -> Unit)? = null
                    ) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = CenterVertically
    ) {
        Text(text = label, maxLines = 1, fontSize = 12.sp)
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }

}

@Composable
private fun RelayScreenFab(
    modifier: Modifier = Modifier,
    isFabExpanded: Boolean = false,
    onFabExpandedOrCollapsed: () -> Unit = {},
    onAddRelay: () -> Unit = {},
    onBackupRelays: () -> Unit = {},
    onResetRelayList: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = isFabExpanded,
            enter = slideInVertically()
                    + fadeIn() + expandIn(),
            exit = slideOutVertically()
                    + fadeOut() + shrinkOut()
        ) {
            ExtendedFloatingActionButton(
                text = { Text(text = "Add Relay") },
                onClick = onAddRelay,
                icon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add relay")
                }
            )
        }
        Spacer(modifier = Modifier.size(5.dp))
        AnimatedVisibility(
            visible = isFabExpanded,
            enter = slideInVertically()
                    + fadeIn() + expandIn(),
            exit = slideOutVertically()
                    + fadeOut() + shrinkOut()
        ) {

            ExtendedFloatingActionButton(
                text = { Text(text = "Backup relays") },
                onClick = onBackupRelays,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Backup all relays"
                    )
                }
            )
        }
        Spacer(modifier = Modifier.size(5.dp))
        AnimatedVisibility(
            visible = isFabExpanded,
            enter = slideInVertically()
                    + fadeIn() + expandIn(),
            exit = slideOutVertically()
                    + fadeOut() + shrinkOut()
        ) {

            ExtendedFloatingActionButton(
                text = { Text(text = "Reset to Default") },
                onClick = onResetRelayList,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.RestartAlt,
                        contentDescription = "Backup all relays"
                    )
                }
            )
        }
        Spacer(modifier = Modifier.size(5.dp))

        FloatingActionButton(
            onClick = onFabExpandedOrCollapsed,
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun RelayComponentPreview() {

    NoskycomposeTheme {
        RelayComponent(
            relayUrl = "wss://nostr-relay.freeberty.net"
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun RelayManagementScreenPreview() {
    NoskycomposeTheme {
        RelayManagementSettingsScreen()
    }
}