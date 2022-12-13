package kt.nostr.nosky_compose.profile

import android.content.Context
import androidx.core.content.edit
import kt.nostr.nosky_compose.profile.model.Profile
import kt.nostr.nosky_compose.utility_functions.*

interface ProfileProvider {
    fun saveProfile(profile: Profile)
    fun getProfile(pubkey: String = ""): Profile
//    fun updateProfile(username: String = getProfile().userName,
//                      newBio: String = getProfile().bio,
//                      profileImage: String = getProfile().profileImage,
//                      followers: Int, following: Int)
    fun resetOrDeleteProfile()
    fun profileExists(profile: Profile? = null): Boolean
}

class LocalProfileDataStore(appContext: Context): ProfileProvider {


    private val profilePreferences = appContext.applicationContext
                                        .getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE)

    override fun getProfile(pubkey: String): Profile {

        //TODO: Implement getting followers and following count from NostrService and update it.
        with(profilePreferences) {
            val privKey = getString(PRIVKEY_TAG, "").toString()
            val pubKey = getString(PUBKEY_TAG, "").toString()
            val username = getString(USERNAME_TAG, " ").toString()
            val profileBio = getString(USER_BIO_TAG, " ").toString()
            val profileImage = getString(PROFILE_IMAGE_TAG, "").toString()
            return Profile(pubKey, privKey, username, profileImage, profileBio)
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

    override fun profileExists(profile: Profile?): Boolean {
        return profileDataExists()
    }

    private fun profileDataExists(): Boolean {
        val containsEditableProperties = profilePreferences.contains(USERNAME_TAG)
                                            || profilePreferences.contains(USER_BIO_TAG)
                                            || profilePreferences.contains(PROFILE_IMAGE_TAG)
        return containsEditableProperties && containsIdentityData()
    }

    fun containsIdentityData(): Boolean {
        return profilePreferences.contains(PRIVKEY_TAG) && profilePreferences.contains(PUBKEY_TAG)
    }

}

object LoggedInProfileProvider {
    fun getLoggedProfile(context: Context): Profile {
        val preferences = context.applicationContext
            .getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE)
        with(preferences) {
            val privKey = getString(PRIVKEY_TAG, "").toString()
            val pubKey = getString(PUBKEY_TAG, "").toString()
            val username = getString(USERNAME_TAG, " ").toString()
            val profileBio = getString(USER_BIO_TAG, " ").toString()
            val profileImage = getString(PROFILE_IMAGE_TAG, "").toString()
            return Profile(pubKey, privKey, username, profileImage, profileBio)
        }
    }
}

