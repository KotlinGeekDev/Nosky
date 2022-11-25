package kt.nostr.nosky_compose.profile

import android.content.Context
import androidx.core.content.edit
import kt.nostr.nosky_compose.profile.model.Profile
import kt.nostr.nosky_compose.utility_functions.*

interface ProfileStore {
    fun saveProfile(profile: Profile)
    fun getProfile(): Profile
    fun resetOrDeleteProfile()
}

class ProfileDataStore(private val appContext: Context): ProfileStore {
    private val profilePreferences = appContext.getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE)

    override fun getProfile(): Profile {

        profilePreferences.run {
            val privKey = getString(PRIVKEY_TAG, " ").toString()
            val pubKey = getString(PUBKEY_TAG, " ").toString()
            val username = getString(USERNAME_TAG, " ").toString()
            val profileBio = getString(USER_BIO_TAG, " ").toString()
            val profileImage = getString(PROFILE_IMAGE_TAG, "").toString()
            return Profile(pubKey, privKey, username, profileImage, profileBio, 0, 0)
        }
    }

    override fun saveProfile(profile: Profile){
        profile.let { p ->
            profilePreferences.edit {
                putString(PRIVKEY_TAG, p.privKey)
                putString(PUBKEY_TAG, p.pubKey)
                if (p.userName.isNotBlank()) putString(USERNAME_TAG, p.userName)
                if (p.bio.isNotBlank()) putString(USER_BIO_TAG, p.bio)
                if (p.profileImage.isNotBlank()) putString(PROFILE_IMAGE_TAG, p.profileImage)
            }
        }
    }

    fun updateBio(newBio: String) {
        profilePreferences.edit {
            putString("bio", newBio)
        }
    }

    fun updateUserName(newUserName: String){
        profilePreferences.edit {
            putString("username", newUserName)
        }
    }


    override fun resetOrDeleteProfile(){
        profilePreferences.edit {
            remove(PRIVKEY_TAG)
            remove(PUBKEY_TAG)
            remove(USERNAME_TAG)
            remove(USER_BIO_TAG)
            remove(PROFILE_IMAGE_TAG)

        }
    }

    fun containsIdentityData(): Boolean {
        return profilePreferences.contains(PRIVKEY_TAG) && profilePreferences.contains(PUBKEY_TAG)
    }

}



object EmptyDataStore: ProfileStore {
    override fun saveProfile(profile: Profile) {
        TODO("Not yet implemented")
    }

    override fun getProfile(): Profile {
        TODO("Not yet implemented")
    }

    override fun resetOrDeleteProfile() {
        TODO("Not yet implemented")
    }
}