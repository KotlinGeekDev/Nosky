package kt.nostr.nosky_compose.intro

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import kt.nostr.nosky_compose.reusable_ui_components.GrayText
import kt.nostr.nosky_compose.reusable_ui_components.ThemedText
import kt.nostr.nosky_compose.reusable_ui_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.reusable_ui_components.theme.Purple500
import kt.nostr.nosky_compose.reusable_ui_components.theme.Purple700
import kt.nostr.nosky_compose.settings.backend.AppThemeState

/**
 * TODO:
 *  - Optimize the recomposition here, by playing with
 *    the composable parameters.
 *  - Find a solution for the image choosing UX/upload.
 */

@Composable
fun NewProfileScreen(themeState: AppThemeState,
                     userName: () -> String,
                     onUserNameUpdate: (String) -> Unit = {},
                     userBio: () -> String,
                     onUserBioUpdate: (String) -> Unit = {},
                     profileImageLink: () -> String,
                     onImageLinkUpdate: (String) -> Unit = {},
                     pubkey: String,
                     generatePubkey: () -> Unit,
                     goToLogin:() -> Unit,
                     onProfileCreated:() -> Unit) {

    val scrollState = rememberScrollState()

    val focusManager = LocalFocusManager.current


    val clearFocus = remember {
        { focusManager.clearFocus(force = true)}
    }

    val intermediateFieldActions = remember {
        KeyboardActions {
            this.defaultKeyboardAction(ImeAction.Next)
        }
    }
    val finalFieldActions = remember {
        KeyboardActions {
            clearFocus()
        }
    }

    val backgroundColor: @Composable () -> Color = remember {
        { if (themeState.isDark()) MaterialTheme.colors.surface else Purple500 }
    }
    val textColor: @Composable () -> Color = remember {
        { if (themeState.isDark()) MaterialTheme.colors.primary else Color.White }
    }

    val newProfileBorderColor: @Composable () -> Color = remember {
        { if (userName().isNotBlank() && pubkey.isNotBlank()) Color.White
                else
                    Color.White.copy(alpha = 0.38f)
        }
    }

    ConstraintLayout(modifier = Modifier
        .background(backgroundColor())
        .fillMaxSize()
        .verticalScroll(scrollState)
        .offset { IntOffset(x = 0, y = (-scrollState.value).times(1 / 5)) }) {
        val (topContent, formContent, bottomContent) = createRefs()

        //For the top content(photo selection, and text)
        Column(modifier = Modifier.constrainAs(topContent){
            top.linkTo(parent.top, margin = 40.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ThemedText(text = "New Profile",
                textColor = Color.White,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp))
            ProfileImageSelector()
        }

        // For the form content
        Row(modifier = Modifier.constrainAs(formContent){
            top.linkTo(topContent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Box(
                modifier = Modifier.padding(top = 70.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "@",
                    modifier = Modifier.padding(end = 5.dp),
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
            }
            Column(
                modifier = Modifier.imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                EntryField(
                    data = userName(),
                    onDataUpdate = { onUserNameUpdate(it) },
                    fieldName = "Username",
                    fieldHint = "For ex.: fiatjaf",
                    fieldKeyboardOptions = fieldKeyboardOptions,
                    fieldKeyboardActions = intermediateFieldActions
                )
//                Spacer(modifier = Modifier.height(20.dp))
//                EntryField(
//                    fieldName = "Display name",
//                    fieldHint = "For ex.: Fiatjaf",
//                    fieldKeyboardOptions = fieldKeyboardOptions,
//                    fieldKeyboardActions = intermediateFieldActions
//                )
                Spacer(modifier = Modifier.height(20.dp))
                EntryField(
                    data = userBio(),
                    onDataUpdate = { onUserBioUpdate(it) },
                    fieldName = "About",
                    fieldHint = "For ex.:Came up with Nostr",
                    fieldKeyboardOptions = fieldKeyboardOptions,
                    fieldKeyboardActions = intermediateFieldActions
                )
                Spacer(modifier = Modifier.height(20.dp))
                EntryField(
                    data = profileImageLink(),
                    onDataUpdate = { onImageLinkUpdate(it) },
                    fieldName = "Profile Image Link",
                    fieldKeyboardActions = finalFieldActions
                )
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = pubkey,
                    onValueChange = {},
                    modifier = Modifier
                        .widthIn(max = TextFieldDefaults.MinWidth),
                    readOnly = true,
                    singleLine = true,
                    colors = TextFieldDefaults
                        .outlinedTextFieldColors(
                            textColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = Color.White.copy(alpha = ContentAlpha.medium),
                            unfocusedBorderColor = Color.White.copy(alpha = ContentAlpha.medium)
                        )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Button(onClick = { generatePubkey() }) {
                    Text(text = "Generate identity")
                }
            }
        }

        //For the bottom buttons
        Column(
            modifier = Modifier
                .constrainAs(bottomContent) {
                    top.linkTo(formContent.bottom, margin = 60.dp)
                    //bottom.linkTo(parent.bottom, margin = 80.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(start = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                onClick = { onProfileCreated() },
                modifier = Modifier
                    //.widthIn(max = TextFieldDefaults.MinWidth)
                    .fillMaxWidth(0.5f),
                enabled = userName().isNotBlank() && pubkey.isNotBlank(),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(2.dp, newProfileBorderColor()),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = backgroundColor(),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Create Profile")
            }
            Spacer(modifier = Modifier.fillMaxWidth(0.5f))
            TextButton(onClick = { goToLogin() },
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))) {
                Text(
                    text = "Want to login instead?",
                    color = textColor()
                )
            }
        }

    }

}

@Composable
private fun ProfileImageSelector() {


//    Image(
//        rememberVectorPainter(image = Icons.Default.PhotoCamera),
//        contentDescription = "App Logo",
//        modifier = Modifier
//            .size(90.dp)
//            .border(
//                border = BorderStroke(width = 3.dp, color = MaterialTheme.colors.onSurface),
//                shape = CircleShape
//            ).clip(CircleShape)
//            .background(brush = Brush
//                .horizontalGradient(listOf(MaterialTheme.colors.primary,
//                                        MaterialTheme.colors.primaryVariant))),
//        contentScale = ContentScale.Inside,
//        //colorFilter = ColorFilter.tint(LocalContentColor.current.copy(alpha = LocalContentAlpha.current))
//    )
    Icon(imageVector = Icons.Default.PhotoCamera,
        contentDescription = null,
        modifier = Modifier
            .size(90.dp)
            .border(
                border = BorderStroke(width = 3.dp, color = Color.White),
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        Purple500,
                        Purple700
                    )
                )
            )
            .scale(0.5f),
        tint = Color.White
    )
}

@Composable
private fun EntryField(
    data: String,
    onDataUpdate: (String) -> Unit,
    fieldName: String,
    fieldHint: String = "",
    fieldKeyboardOptions: KeyboardOptions = remember {
        KeyboardOptions() },
    fieldKeyboardActions: KeyboardActions = remember {
        KeyboardActions() }
    ) {

    val clipboardManager = LocalClipboardManager.current

    val fieldDescription by remember {
        derivedStateOf {
            fieldHint.ifBlank { "Enter the $fieldName here..." }
        }
    }

    Column {
        Row {
            Text(text = fieldName, color = Color.White,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp))
            Spacer(modifier = Modifier.width(5.dp))
            if (fieldName != "Username"){
                GrayText(text = "optional")
            }
        }

        OutlinedTextField(value = data,
            onValueChange = { value -> onDataUpdate(value) },
            modifier = Modifier
                .widthIn(max = TextFieldDefaults.MinWidth)
                .selectable(true, onClick = {}),
            placeholder = { Text(text = fieldDescription,
                color = Color.White.copy(alpha = 0.6f), maxLines = 1) },
            trailingIcon = { Icon(
                imageVector = Icons.Default.ContentPaste,
                contentDescription = "Copy the $fieldName",
                modifier = Modifier.clickable {
                       onDataUpdate(clipboardManager.getText().toString())
                },
                tint = Color.White
            ) },
            keyboardOptions = fieldKeyboardOptions,
            keyboardActions = fieldKeyboardActions,
            singleLine = true,
            colors = TextFieldDefaults
                .outlinedTextFieldColors(
                    textColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White.copy(alpha = ContentAlpha.medium),
                    unfocusedBorderColor = Color.White.copy(alpha = ContentAlpha.medium)
                )
        )
    }
}

private val fieldKeyboardOptions = KeyboardOptions(
    autoCorrect = false,
    keyboardType = KeyboardType.Ascii,
    imeAction = ImeAction.Next
)

@Preview
@Composable
private fun NewProfileScreenPreview() {
    val appTheme = AppThemeState(true)
    val testName = remember {
        mutableStateOf("")
    }
    val testBio = remember {
        mutableStateOf("")
    }
    val profileImage = remember {
        mutableStateOf("")
    }
    NoskycomposeTheme(darkTheme = appTheme.isDark()) {
        NewProfileScreen(
            themeState = appTheme,
            userName = { testName.value },
            onUserNameUpdate = { testName.value = it },
            userBio = { testBio.value },
            onUserBioUpdate = { testBio.value = it },
            profileImageLink = { profileImage.value },
            onImageLinkUpdate = { profileImage.value = it },
            pubkey = "",
            generatePubkey = {},
            goToLogin = {},
            onProfileCreated = {})
    }
}