package kt.nostr.nosky_compose.profile

import kt.nostr.nosky_compose.profile.model.Profile

object EmptyDataStore: ProfileProvider {
    override fun saveProfile(profile: Profile) {
        TODO("Not yet implemented")
    }

    override fun getProfile(pubkey: String): Profile {
        TODO("Not yet implemented")
    }

    override fun resetOrDeleteProfile() {
        TODO("Not yet implemented")
    }

    override fun profileExists(profile: Profile?): Boolean {
        TODO("Not yet implemented")
    }
}