package kt.nostr.nosky_compose.utility_functions

import android.text.util.Linkify.TransformFilter
import android.util.Patterns
import java.util.regex.Pattern

fun String.isURL(): Boolean {
    val urlPattern = Regex("((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)")
    val contains = this.contains(urlPattern)
    return contains
    //URLUtil.isValidUrl(text)
}

val textTransformFilter = TransformFilter(){ match, _ ->
    match.group()
}

val mentionsPattern = Pattern.compile("@([A-Za-z0-9_-]+)")
val hashTagsPattern = Pattern.compile("#([A-Za-z0-9_-]+)")
val urlPattern = Patterns.WEB_URL