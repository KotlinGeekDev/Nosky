package kt.nostr.nosky_compose.intro

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import kt.nostr.nosky_compose.reusable_components.GrayText
import kt.nostr.nosky_compose.reusable_components.ThemedText
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.reusable_components.theme.Purple500
import kt.nostr.nosky_compose.reusable_components.theme.Purple700
import kt.nostr.nosky_compose.settings.backend.AppThemeState

/**
 * TODO:
 *  - Modify the composable parameters to account for the
 *   data given by the new user, and hoot it up to the
 *   Profile model.(make it look like WelcomeScreen, with tweaks)
 *  - Find a solution for the image choosing UX/upload.
 */

@Composable
fun NewProfileScreen(themeState: AppThemeState,
                     userName: String = "",
                     onUpdateUserName: (String) -> Unit = {},
                     profileImageLink: String = "",
                     onImageLinkUpdate: (String) -> Unit = {},
                     userBio: String = "",
                     onUserBioUpdate: (String) -> Unit = {},
                     onLoginClicked:() -> Unit,
                     onProfileCreated:() -> Unit) {

    val scrollState = rememberScrollState()

    val backgroundColor: @Composable () -> Color = remember {
        { if (themeState.isDark()) MaterialTheme.colors.surface else Purple500 }
    }
    val textColor: @Composable () -> Color = remember {
        { if (themeState.isDark()) MaterialTheme.colors.primary else Color.White }
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
            top.linkTo(topContent.bottom, margin = 30.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Box(
                modifier = Modifier.padding(top = 80.dp)
            //    contentAlignment = Alignment.TopCenter
            ) {
                Text(text = "@",
                    modifier = Modifier.padding(end = 5.dp),
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
            }
            Column(modifier = Modifier.imePadding(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Spacer(modifier = Modifier.height(30.dp))
                EntryField(fieldName = "Username", fieldHint = "For ex.: fiatjaf")
                Spacer(modifier = Modifier.height(20.dp))
                EntryField(fieldName = "Display name", fieldHint = "For ex.: Fiatjaf")
                Spacer(modifier = Modifier.height(20.dp))
                EntryField(fieldName = "About", fieldHint = "For ex.:Came up with Nostr")
                Spacer(modifier = Modifier.height(20.dp))
                EntryField(fieldName = "Profile Image Link")
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        //For the bottom buttons
        Column(modifier = Modifier.constrainAs(bottomContent){
            top.linkTo(formContent.bottom)
            bottom.linkTo(parent.bottom, 15.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        },
            verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedButton(onClick = onProfileCreated,
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = backgroundColor())) {
                Text(text = "Create Profile", color = Color.White)
            }
            Spacer(modifier = Modifier.fillMaxWidth(0.5f))
            TextButton(onClick = onLoginClicked , border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))) {
                Text(text = "Want to login instead?",
                    color = textColor())
            }
        }

    }

}

@Composable
fun ProfileImageSelector() {


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
fun EntryField(fieldName: String, fieldHint: String = "") {
    var enteredKey by remember {
        mutableStateOf("")
    }
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

        OutlinedTextField(value = enteredKey,
            onValueChange = { enteredKey = it },
            modifier = Modifier
                .widthIn(max = TextFieldDefaults.MinWidth)
                .selectable(true, onClick = {}),
            placeholder = { Text(text = fieldDescription,
                color = Color.White.copy(alpha = 0.6f), maxLines = 1) },
            trailingIcon = { Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy the $fieldName",
                modifier = Modifier.clickable {},
                tint = Color.White
            ) },
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

@Preview
@Composable
fun NewProfileScreenPreview() {
    val appTheme = AppThemeState(true)
    NoskycomposeTheme(darkTheme = appTheme.isDark()) {
        NewProfileScreen(themeState = appTheme, onLoginClicked = {}, onProfileCreated = {})
    }
}