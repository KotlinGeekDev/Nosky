package kt.nostr.nosky_compose.main_backend

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kt.nostr.nosky_compose.utility_functions.APP_TAG
import kt.nostr.nosky_compose.utility_functions.misc.currentSystemUnixTimeStamp
import nostr.postr.Client
import nostr.postr.JsonFilter
import nostr.postr.Relay
import nostr.postr.events.Event
import nostr.postr.events.MetadataEvent
import nostr.postr.events.TextNoteEvent
import nostr.postr.toHex
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

object NostrService {

    private const val feedEventsLimit = 20

    private val nostrClient = Client



    fun sendPost(postContent: String,
                 timestamp: Long,
                 privateKey: ByteArray,
                 replyTos: List<String> = listOf()
    ){
        val event = TextNoteEvent
            .create(
                msg = postContent,
                privateKey = privateKey,
                createdAt = timestamp,
                replyTos = replyTos.ifEmpty { null }, mentions = null)
        nostrClient.send(event)
    }


    fun getTextEvents(pubkeyList: List<String> = emptyList(),
                      eventId: String = ""
                    ): Flow<TextNoteEvent> = channelFlow {

        val timeNow = currentSystemUnixTimeStamp()
        val feedListener = object : Client.Listener() {

            override fun onEvent(event: Event, relay: Relay) {
                when(event){
                    is TextNoteEvent -> {
                        event.run {
                            Log.i(APP_TAG, "Found Event: Id ->${id.toHex()} content -> $content")
                            trySend(this)
                        }
                    }
                }
            }

            override fun onRelayStateChange(type: Relay.Type, relay: Relay) {
                if (type == Relay.Type.EOSE) {
                    Log.i(APP_TAG, "TextEvents flow ->Relay done sending.")
                    Client.unsubscribe(this)
                    close()
                }
            }


        }
        Log.i(APP_TAG,"Launching feed subscriber...")
        Client.subscribe(feedListener)
        Log.i(APP_TAG,"Connecting to feed sub...")
        Client.connect(mutableListOf(
            JsonFilter(
                kinds = listOf(TextNoteEvent.kind),
                authors = pubkeyList.ifEmpty { null },
                tags = if (eventId.isBlank()) null else mapOf("e" to listOf(eventId, "reply")),
                since = currentSystemUnixTimeStamp() - (60*60*24),
                limit = feedEventsLimit
            ),
            //JsonFilter(kinds = listOf(MetadataEvent.kind))
        ),
           // relays = arrayOf(Relay("wss://relay.nostr.info"), Relay("wss://relay.damus.io"))
        )
        Log.i(APP_TAG,"Retrieving data from feed sub...")
        delay(1000)
        Log.i(APP_TAG, "Elapsed time :${currentSystemUnixTimeStamp() -timeNow}s")

        awaitClose {
            Log.i(APP_TAG,"Disconnecting feed sub...")
            Client.unsubscribe(feedListener)


            Log.i(APP_TAG,"Disconnected from feed sub.")
        }
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalTime::class)
    fun getProfilesInfo(pubkeyList: List<String> = listOf()): Flow<MetadataEvent> = channelFlow {
        val replyEventCount = AtomicInteger(0)
        val upperLimit = if (pubkeyList.isEmpty()) feedEventsLimit else pubkeyList.size
        val timeNow = TimeSource.Monotonic.markNow()
        val profilesListener = object: Client.Listener() {
            override fun onNewEvent(event: Event) {
               // Log.i(APP_TAG, "Found Kind-0 event -> ${event.content}")
                (event as MetadataEvent).run {

                    Log.i(APP_TAG,"Found User: name ->${contactMetaData.name} bio -> ${contactMetaData.about}")

                        replyEventCount.getAndIncrement()
                        trySend(this)
                }


            }

            override fun onRelayStateChange(type: Relay.Type, relay: Relay) {
                when(type){
                    Relay.Type.EOSE -> {

//                        Log.i(APP_TAG, "Profile flow ->Relay done sending.")
//                        Log.i(APP_TAG,"Disconnecting profile sub...")
//                        nostrClient.unsubscribe(this)
//                        Log.i(APP_TAG,"Disconnected from profile sub.")
//                       // close()
                    }
                    else -> {}
                }
            }

        }

        Log.i(APP_TAG,"Launching profile subscriber...")
        nostrClient.subscribe(profilesListener)
        Log.i(APP_TAG,"Connecting to profile sub...")
        val filter = JsonFilter(
            kinds = listOf(MetadataEvent.kind),
            authors = pubkeyList.map { it.take(10) }.ifEmpty { null },
            limit = upperLimit)
        println("Profile filter: $filter")

        nostrClient.connect(mutableListOf(filter),
       //    relays = arrayOf(Relay("wss://relay.nostr.info"))
//                //Relay("wss://relay.damus.io"),
//                Relay("wss://nostr.bitcoiner.social"),
//                Relay("wss://nostr.rocks"),
//                Relay("wss//nostr-pub.semisol.dev"))
            )
        println("Retrieving data from profile sub...")
        while (replyEventCount.get() < upperLimit){
            Log.i(APP_TAG, "Kind-0 count: ${replyEventCount.get()}")
            if (replyEventCount.get() == upperLimit) {
                close()
            }
        }

        awaitClose {
            Log.i(APP_TAG,"Disconnecting profile sub...")
            nostrClient.unsubscribe(profilesListener)
            Log.i(APP_TAG,"Disconnected from profile sub.")

        //close()
        }

    }.flowOn(Dispatchers.IO)

}