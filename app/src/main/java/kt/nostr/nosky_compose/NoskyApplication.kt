package kt.nostr.nosky_compose

import android.app.Application
import kt.nostr.nosky_compose.profile.model.Profile

class NoskyApplication: Application() {
    val userProfile = Profile()


}

