package kt.nostr.nosky_compose.settings.backend

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class NostrRelay(val url: String, val readPolicy: Boolean, val writePolicy: Boolean)

val relayList = listOf(
    NostrRelay("wss://nostr-relay.untethr.me", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr-relay.freeberty.net", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr.bitcoiner.social", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr-relay.wlvs.space", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr-pub.wellorder.net", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr.rocks", readPolicy = true, writePolicy = true),
    NostrRelay("wss://nostr.onsats.org", readPolicy = true, writePolicy = true),
    NostrRelay("wss://relay.damus.io", readPolicy = true, writePolicy = true)
)