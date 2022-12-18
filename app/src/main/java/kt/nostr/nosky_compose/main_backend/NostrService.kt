package kt.nostr.nosky_compose.main_backend

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
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
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

class NostrService(){

    private val feedEventsLimit = 10

    private val nostrClient = Client

    val replyEventCount = AtomicInteger(0)
    val replies: MutableList<TextNoteEvent> = mutableListOf()
    private val repliesListener = object: Client.Listener() {
        override fun onEvent(event: Event, relay: Relay) {
            when(event){
                is TextNoteEvent -> {
                    replies.add(event)
                    Log.i(APP_TAG,"Obtained reply: ${event.content} \n Replying to: ${event.replyTos.first()}")
                    replyEventCount.getAndIncrement()
                }
            }
        }

        override fun onRelayStateChange(type: Relay.Type, relay: Relay) {
            when(type){
                Relay.Type.EOSE -> nostrClient.unsubscribe(this)
                else ->{}
            }
        }

    }

    fun sendPost(postContent: String, timestamp: Long, privateKey: ByteArray){
        val event = TextNoteEvent
            .create(
                msg = postContent,
                privateKey = privateKey,
                createdAt = timestamp, replyTos = null, mentions = null)
        nostrClient.send(event)
    }

    @OptIn(ExperimentalTime::class)
    suspend fun getReplies(eventId: String): List<TextNoteEvent> = withContext(Dispatchers.IO){
        val timeSource = TimeSource.Monotonic
        val current = timeSource.markNow()
        check(replyEventCount.get() == 0)
        Log.i(APP_TAG,"Launching replies subscriber...")
        nostrClient.subscribe(repliesListener)
        Log.i(APP_TAG, "Connecting to replies sub...")
        nostrClient.connect(
            mutableListOf(JsonFilter(ids = listOf(eventId),
                kinds = listOf(TextNoteEvent.kind),
                tags = mapOf("e" to listOf("reply")),
                limit = feedEventsLimit + feedEventsLimit))
        )
        Log.i(APP_TAG,"Retrieving data from replies sub...")
        while (replyEventCount.get() <= feedEventsLimit - 5){
            delay(150)
            Log.i(APP_TAG,"replies sub: $replyEventCount")
            if (replyEventCount.get() == feedEventsLimit){
                nostrClient.unsubscribe(repliesListener)
                replyEventCount.set(0)
            }
        }
        if (replies.isEmpty())  emptyList()
        else replies.toList()
    }


    @OptIn(ExperimentalTime::class)
    fun getTextEvents(): Flow<TextNoteEvent> = channelFlow {
        val incomingEventCount = AtomicInteger(0)
        val timeNow = TimeSource.Monotonic.markNow()
        val feedListener = object : Client.Listener() {

            override fun onEvent(event: Event, relay: Relay) {
                when(event){
                    is TextNoteEvent -> {
                        event.run {

                                Log.i(APP_TAG, "Found Event: Id ->${event.id.toHex()} content -> ${event.content}")
                                trySend(this)
                                incomingEventCount.getAndIncrement()
                                //println("From feed sub: $incomingEventCount")
                        }
                    }
//                    is MetadataEvent -> {
//                        event.run {
//                            println("Found User: name ->${contactMetaData.name} bio -> ${contactMetaData.about}")
//                            trySend(this)
//                            incomingEventCount.getAndIncrement()
//                        }
//                    }
                }
            }

            override fun onRelayStateChange(type: Relay.Type, relay: Relay) {
                if (type == Relay.Type.EOSE) {
                   // Client.unsubscribe(this)
                }
            }
        }
        Log.i(APP_TAG,"Launching feed subscriber...")
        nostrClient.subscribe(feedListener)
        Log.i(APP_TAG,"Connecting to feed sub...")
        nostrClient.connect(mutableListOf(
            JsonFilter(
                kinds = listOf(TextNoteEvent.kind),
                since = currentSystemUnixTimeStamp() - (60*60*5),
                //until = currentSystemUnixTimeStamp(),
                limit = feedEventsLimit
            ),
            //JsonFilter(kinds = listOf(MetadataEvent.kind))
        ),
           // relays = arrayOf(Relay("wss://relay.nostr.info"), Relay("wss://relay.damus.io"))
        )
        Log.i(APP_TAG,"Retrieving data from feed sub...")

        while (incomingEventCount.get() <= feedEventsLimit){
            delay(1000)
            Log.i(APP_TAG,"feed sub: $incomingEventCount")
            Log.i(APP_TAG, "Elapsed time :${timeNow.elapsedNow().inWholeSeconds}s")
            if (
                //incomingEventCount.get() == feedEventsLimit
                timeNow.elapsedNow() == 10.seconds
            ) {
                Log.i(APP_TAG,"Disconnecting feed sub...")
                nostrClient.unsubscribe(feedListener)
                incomingEventCount.set(0)
                Log.i(APP_TAG,"Disconnected from feed sub.")


            }
        }

    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalTime::class)
    fun getProfilesInfo(pubkeyList: List<String> = listOf()): Flow<MetadataEvent> = channelFlow {
        var incomingEventCount = 0
        val timeNow = TimeSource.Monotonic.markNow()
        val profilesListener = object: Client.Listener() {
            override fun onNewEvent(event: Event) {
                    (event as MetadataEvent).run {
                        Log.i(APP_TAG,"Found User: name ->${contactMetaData.name} bio -> ${contactMetaData.about}")
                        trySend(this)
                        incomingEventCount++
                    }
                    Log.i(APP_TAG, "From profile sub: $incomingEventCount")
                }
        }

        Log.i(APP_TAG,"Launching profile subscriber...")
        Client.subscribe(profilesListener)
        Log.i(APP_TAG,"Connecting to profile sub...")
        val filter = JsonFilter(
            kinds = listOf(MetadataEvent.kind),
            authors = pubkeyList.ifEmpty { null },
            limit = feedEventsLimit + feedEventsLimit)
        println("Profile filter: $filter")
        Client.connect(mutableListOf(filter),
//            relays = arrayOf(Relay("wss://relay.nostr.info"),
//                //Relay("wss://relay.damus.io"),
//                Relay("wss://nostr.bitcoiner.social"),
//                Relay("wss://nostr.rocks"),
//                Relay("wss//nostr-pub.semisol.dev"))
            )
        println("Retrieving data from profile sub...")
        while (incomingEventCount <= feedEventsLimit){
            delay(1000)
            Log.i(APP_TAG,"profile sub: $incomingEventCount")
            if (
                timeNow.elapsedNow() == 10.seconds
               // incomingEventCount == feedEventsLimit
            ) {
                Log.i(APP_TAG,"Disconnecting profile sub...")
                Client.unsubscribe(profilesListener)
                incomingEventCount = 0
                Log.i(APP_TAG,"Disconnected from profile sub.")

            }
        }

    }.flowOn(Dispatchers.IO)

    companion object {
        fun get(): NostrService = NostrService()
    }
}