package kt.nostr.nosky_compose.utility_functions.datetime

import ktnostr.currentSystemUnixTimeStamp

fun timeAgoFrom(timestamp: Long): String {

    val currentTime = currentSystemUnixTimeStamp()//Date().time;
    val timeDiff = currentTime - timestamp;
    when {
        timeDiff >= (60 * 60 * 24) -> {
            // Days
            return "${timeDiff / (60 * 60 * 24)}d";
        }
        timeDiff >= (60 * 60) -> {
            // Hours
            return "${timeDiff / (60 * 60)}h";
        }
        timeDiff >= (60) -> {
            // Minutes
            return "${timeDiff / (60)}m";
        }
        timeDiff >= 1000 -> {
            // Seconds
            return "${timeDiff / 1000}s";
        }
        else -> return "0s"
    }
}