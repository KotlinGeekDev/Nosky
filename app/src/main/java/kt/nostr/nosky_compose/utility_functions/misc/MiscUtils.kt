package kt.nostr.nosky_compose.utility_functions.misc

fun isPrime(number: Int): Boolean {
    for (potentialFactor in 2..number/2){
        if (potentialFactor.isFactorOf(number)) return false
    }
    return true
}

fun Int.isFactorOf(number: Int): Boolean {
    return number % this == 0
}
