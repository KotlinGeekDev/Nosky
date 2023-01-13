package kt.nostr.nosky_compose.settings.backend

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import nostr.postr.Constants

@Stable
@Serializable
data class NostrRelay(val url: String, val readPolicy: Boolean, val writePolicy: Boolean)

val relayList = Constants.defaultRelays.map {
    NostrRelay(it.url, it.read, it.write)
}