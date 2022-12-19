package kt.nostr.nosky_compose.main_backend

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

class NostrService(){

    private val feedEventsLimit = 10

    private val nostrClient = Client

    val replyEventCount = AtomicInteger(0)
    private val replies: MutableList<TextNoteEvent> = mutableListOf()

    fun sendPost(postContent: String, timestamp: Long, privateKey: ByteArray){
        val event = TextNoteEvent
            .create(
                msg = postContent,
                privateKey = privateKey,
                createdAt = timestamp, replyTos = null, mentions = null)
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
                        replyEventCount.getAndIncrement()
                    }
                    else -> {
                        Log.i(APP_TAG, "Replies sub::Obtained event -> ${event.content}")
                    }
                }
            }

            override fun onRelayStateChange(type: Relay.Type, relay: Relay) {
                when(type){
                    Relay.Type.EOSE -> {
                        Log.i(APP_TAG, "Replies flow ->Relay done sending.")
                        nostrClient.unsubscribe(this)
                        close()
                    }
                    else ->{}
                }
            }

//            override fun onError(error: Error, relay: Relay) {
//                cancel(error.message?: "Relay Error")
//            }

        }

        val timeSource = TimeSource.Monotonic
        val current = timeSource.markNow()
        check(replyEventCount.get() == 0)

            Log.i(APP_TAG,"Launching replies subscriber...")
            nostrClient.subscribe(repliesListener)
            Log.i(APP_TAG, "Connecting to replies sub...")
        val repliesFilter = JsonFilter(
            kinds = listOf(TextNoteEvent.kind),
            tags = mapOf("e" to listOf(eventId.take(10))),
            limit = feedEventsLimit - 7)
        Log.i(APP_TAG, "Replies Filter -> $repliesFilter")
            nostrClient.connect(
                mutableListOf(repliesFilter)
            )
            Log.i(APP_TAG,"Retrieving data from replies sub...")
            while (replyEventCount.get() <= (feedEventsLimit - 5)){
                delay(1000)
                Log.i(APP_TAG,"replies sub: $replyEventCount")
                if (replyEventCount.get() == feedEventsLimit|| current.elapsedNow() == 5.seconds){
                    Log.i(APP_TAG,"Disconnecting replies sub...")
                    nostrClient.unsubscribe(repliesListener)
                    replyEventCount.set(0)
                    cancel("Either no replies or taking too much time.")
                    Log.i(APP_TAG,"Disconnected replies sub.")
                }
            }

    }


    @OptIn(ExperimentalTime::class)
    fun getTextEvents(pubkeyList: List<String> = emptyList()): Flow<TextNoteEvent> = channelFlow {
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
                since = currentSystemUnixTimeStamp() - (60*60*5),
                //until = currentSystemUnixTimeStamp(),
                limit = feedEventsLimit*2
            ),
            //JsonFilter(kinds = listOf(MetadataEvent.kind))
        ),
           // relays = arrayOf(Relay("wss://relay.nostr.info"), Relay("wss://relay.damus.io"))
        )
        Log.i(APP_TAG,"Retrieving data from feed sub...")

        while (incomingEventCount.get() <= feedEventsLimit*2){
            delay(1000)
            Log.i(APP_TAG,"feed sub: $incomingEventCount")
            Log.i(APP_TAG, "Elapsed time :${timeNow.elapsedNow().inWholeSeconds}s")
            if (
                incomingEventCount.get() == feedEventsLimit ||
                timeNow.elapsedNow().inWholeSeconds == 10L
            ) {
                Log.i(APP_TAG,"Disconnecting feed sub...")
                Client.unsubscribe(feedListener)
                Client.disconnect()
                incomingEventCount.set(0)
                Log.i(APP_TAG,"Disconnected from feed sub.")


            }
        }

    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalTime::class)
    fun getProfilesInfo(pubkeyList: List<String> = listOf()): Flow<MetadataEvent> = channelFlow {
        var incomingEventCount = 0
        val upperLimit = if (pubkeyList.isEmpty()) feedEventsLimit else pubkeyList.size
        val timeNow = TimeSource.Monotonic.markNow()
        val profilesListener = object: Client.Listener() {
            override fun onNewEvent(event: Event) {

                (event as MetadataEvent).run {
                    Log.i(APP_TAG,"Found User: name ->${contactMetaData.name} bio -> ${contactMetaData.about}")
                    trySend(this)
                    Log.i(APP_TAG, "From profile sub: $incomingEventCount")
                    check(incomingEventCount != -1)
                    incomingEventCount++
                }

            }

//            override fun onRelayStateChange(type: Relay.Type, relay: Relay) {
//                when(type){
//                    Relay.Type.EOSE -> {
//                        Log.i(APP_TAG, "Profile flow ->Relay done sending.")
//                        Log.i(APP_TAG,"Disconnecting profile sub...")
//                        nostrClient.unsubscribe(this)
//                        Log.i(APP_TAG,"Disconnected from profile sub.")
//                        incomingEventCount = -1
//
//                    }
//                    else -> {}
//                }
//            }

    //            override fun onError(error: Error, relay: Relay) {
    //                cancel(error.message?: " Some error")
    //                super.onError(error, relay)
    //            }
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
       //     relays = arrayOf(Relay("wss://relay.nostr.info"))
//                //Relay("wss://relay.damus.io"),
//                Relay("wss://nostr.bitcoiner.social"),
//                Relay("wss://nostr.rocks"),
//                Relay("wss//nostr-pub.semisol.dev"))
            )
        println("Retrieving data from profile sub...")

        while (incomingEventCount < upperLimit){
            delay(1000)
            Log.i(APP_TAG,"profile sub: $incomingEventCount")
            if (
                timeNow.elapsedNow() == 10.seconds ||
                incomingEventCount == -1
            ) {
                Log.i(APP_TAG,"Disconnecting profile sub...")
                nostrClient.unsubscribe(profilesListener)
                Log.i(APP_TAG,"Disconnected from profile sub.")
                //close()
            }

        }

    }.flowOn(Dispatchers.IO)

    companion object {
        fun get(): NostrService = NostrService()
    }
}