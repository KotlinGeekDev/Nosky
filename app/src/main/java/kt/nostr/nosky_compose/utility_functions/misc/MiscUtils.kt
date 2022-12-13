package kt.nostr.nosky_compose.utility_functions.misc

import fr.acinq.secp256k1.Hex
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun isPrime(number: Int): Boolean {
    for (potentialFactor in 2..number/2){
        if (potentialFactor.isFactorOf(number)) return false
    }
    return true
}

fun Int.isFactorOf(number: Int): Boolean {
    return number % this == 0
}

/**
 * The function takes a Unix timestamp in and returns
 * a human-readable date and time.
 * @param timestamp The Unix timestamp as a Long
 * @return A human-readable date and time, as a string.
 */

fun formattedDateTime(timestamp: Long): String {
    return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("uuuu MMM d hh:mm a"))
}


/**
 * The functions below return the current Unix timestamp.
 * They are technically equivalent.
 * Could there be hidden differences?
 */

fun currentUnixTimeStampFromInstant(): Long  = Instant.now().epochSecond
fun currentSystemUnixTimeStamp(): Long = System.currentTimeMillis().div(1000L)

fun ByteArray.toHexString() = Hex.encode(this)

enum class LinkInfo(val description: String, val link:String){
    SourceCode(description = "Source Code", link =  "https://github.com/AnonymousGeekDev/Nosky"),
    Telegram(description = "Telegram", link = "https://t.me/nostr_protocol"),
    Matrix(description = "Matrix(unofficial)", link = "https://matrix.to/#/#nostr-lobby:matrix.org")
}



