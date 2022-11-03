package kt.nostr.nosky_compose.utility_functions

fun String.isURL(): Boolean {
    val urlPattern = Regex("((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)")
    val contains = this.contains(urlPattern)
    return contains
    //URLUtil.isValidUrl(text)
}