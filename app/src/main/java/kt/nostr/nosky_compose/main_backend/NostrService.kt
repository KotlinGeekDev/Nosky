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

    private val feedEventsLimit = 20

    private val nostrClient = Client

    val replyEventCount = AtomicInteger(0)

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

    @OptIn(ExperimentalTime::class)
    fun getReplies(eventId: String): Flow<TextNoteEvent> = channelFlow {
        val repliesListener = object: Client.Listener() {
            override fun onEvent(event: Event, relay: Relay) {
                when(event){
                    is TextNoteEvent -> {
                        Log.i(APP_TAG,"Obtained reply: ${event.content} \n Replying to: ${event.replyTos.first()}")
                        trySend(event)
                    }

                }
            }

//            override fun onRelayStateChange(type: Relay.Type, relay: Relay) {
//                when(type){
//                    Relay.Type.EOSE -> {
//                        Log.i(APP_TAG, "Replies flow ->Relay done sending.")
//                        nostrClient.unsubscribe(this)
//                        close()
//                    }
//                    else ->{}
//                }
//            }

        }

        val timeSource = TimeSource.Monotonic
        val current = timeSource.markNow()

        Log.i(APP_TAG,"Launching replies subscriber...")
        Client.subscribe(repliesListener)
        Log.i(APP_TAG, "Connecting to replies sub...")
        val repliesFilter = JsonFilter(
            kinds = listOf(TextNoteEvent.kind),
            tags = mapOf("e" to listOf(eventId)),
            limit = feedEventsLimit - 5)
        Log.i(APP_TAG, "Replies Filter -> $repliesFilter")
            Client.connect(
                mutableListOf(repliesFilter)
            )
        Log.i(APP_TAG,"Retrieving data from replies sub...")
        delay(1000)
       while (replyEventCount.get() < feedEventsLimit){
           Log.i(APP_TAG, "Elapsed time... ${current.elapsedNow().inWholeSeconds}")
           Log.i(APP_TAG,"replies sub: $replyEventCount")
           replyEventCount.getAndIncrement()
           if (replyEventCount.get() == feedEventsLimit){
               Log.i(APP_TAG,"Disconnecting replies sub...")
               Client.unsubscribe(repliesListener)
               replyEventCount.set(0)
               close()
               Log.i(APP_TAG,"Disconnected replies sub.")
           }
       }


        awaitClose {
            Log.i(APP_TAG,"Disconnecting replies sub...")
            Client.unsubscribe(repliesListener)
            replyEventCount.set(0)
            close()
            Log.i(APP_TAG,"Disconnected replies sub.")
        }


    }.flowOn(Dispatchers.IO)


    @OptIn(ExperimentalTime::class)
    fun getTextEvents(pubkeyList: List<String> = emptyList(),
                      eventIdList: List<String> = emptyList()
                    ): Flow<TextNoteEvent> = channelFlow {

        val timeNow = TimeSource.Monotonic.markNow()
        val feedListener = object : Client.Listener() {

            override fun onEvent(event: Event, relay: Relay) {
                when(event){
                    is TextNoteEvent -> {
                        event.run {

                            Log.i(APP_TAG, "Found Event: Id ->${event.id.toHex()} content -> ${event.content}")
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
                tags = if (eventIdList.isEmpty()) null else mapOf("e" to eventIdList),
                since = currentSystemUnixTimeStamp() - (60*60*24),
                limit = feedEventsLimit
            ),
            //JsonFilter(kinds = listOf(MetadataEvent.kind))
        ),
           // relays = arrayOf(Relay("wss://relay.nostr.info"), Relay("wss://relay.damus.io"))
        )
        Log.i(APP_TAG,"Retrieving data from feed sub...")
        delay(1000)
        Log.i(APP_TAG, "Elapsed time :${timeNow.elapsedNow().inWholeSeconds}s")

        awaitClose {
            Log.i(APP_TAG,"Disconnecting feed sub...")
            Client.unsubscribe(feedListener)
            //Client.disconnect()

            Log.i(APP_TAG,"Disconnected from feed sub.")
        }
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalTime::class)
    fun getProfilesInfo(pubkeyList: List<String> = listOf()): Flow<MetadataEvent> = channelFlow {
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