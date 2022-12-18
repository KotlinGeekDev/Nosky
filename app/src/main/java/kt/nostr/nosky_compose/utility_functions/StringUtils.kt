package kt.nostr.nosky_compose.utility_functions

import android.text.util.Linkify.TransformFilter
import android.util.Patterns
import java.util.regex.Pattern

const val APP_TAG = "NoskyApp"

const val PROFILE_DATA = "profile_data"
const val PRIVKEY_TAG = "nsec"
const val PUBKEY_TAG = "npub"
const val USERNAME_TAG = "username"
const val USER_BIO_TAG = "bio"
const val PROFILE_IMAGE_TAG = "profile_image"


fun String.isURL(): Boolean {
    val urlPattern =
        Regex("((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)")
    return contains(urlPattern)
    //URLUtil.isValidUrl(text)
}

fun String.urlsInText(): List<String> {
    val listSubstrings = this.split(" ", ",")
    val links = listSubstrings.filter { substring -> substring.isURL() }
    return links
//        .filter {
//        it.endsWith(".jpg")
//                || it.endsWith(".img")
//                || it.endsWith(".webp")
//                || it.endsWith(".gif")
//    }
}

val textTransformFilter = TransformFilter(){ match, _ ->
    match.group()
}

val mentionsPattern: Pattern = Pattern.compile("@([A-Za-z0-9_-]+)")
val hashTagsPattern: Pattern = Pattern.compile("#([A-Za-z0-9_-]+)")
val urlPattern: Pattern = Patterns.WEB_URL