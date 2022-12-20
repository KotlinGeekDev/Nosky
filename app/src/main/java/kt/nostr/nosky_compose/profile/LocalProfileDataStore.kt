package kt.nostr.nosky_compose.profile

import android.content.Context
import com.liftric.kvault.KVault
import kt.nostr.nosky_compose.profile.model.Profile
import kt.nostr.nosky_compose.utility_functions.*

interface ProfileProvider {
    fun saveProfile(profile: Profile) {

    }
    fun getProfile(pubkey: String = ""): Profile {
       return Profile(pubKey = pubkey)
    }
//    fun updateProfile(username: String = getProfile().userName,
//                      newBio: String = getProfile().bio,
//                      profileImage: String = getProfile().profileImage,
//                      followers: Int, following: Int)
    fun resetOrDeleteProfile() {

    }
    fun profileExists(profile: Profile? = null): Boolean = true
}

class LocalProfileDataStore(appContext: Context): ProfileProvider {

    private val profileVault = KVault(appContext.applicationContext, PROFILE_DATA)



    override fun getProfile(pubkey: String): Profile {


        //TODO: Implement getting followers and following count from NostrService and update it.
        with(profileVault) {
            val privKey = string(PRIVKEY_TAG) ?: ""
            val pubKey = string(PUBKEY_TAG) ?: ""
            val username = string(USERNAME_TAG) ?: ""
            val profileBio = string(USER_BIO_TAG) ?: ""
            val profileImage = string(PROFILE_IMAGE_TAG) ?: ""
            return Profile(pubKey, privKey, username, profileImage, profileBio)
        }
    }

    override fun saveProfile(profile: Profile){
        profile.let { p ->
            with(profileVault) {
                set(PRIVKEY_TAG, p.privKey)
                set(PUBKEY_TAG, p.pubKey)
                if (p.userName.isNotBlank()) set(USERNAME_TAG, p.userName)
                if (p.bio.isNotBlank()) set(USER_BIO_TAG, p.bio)
                if (p.profileImage.isNotBlank()) set(PROFILE_IMAGE_TAG, p.profileImage)
            }
        }
    }

    fun updateBio(newBio: String) {
        profileVault.set("bio", newBio)
    }

    fun updateUserName(newUserName: String){
        profileVault.set("username", newUserName)
    }


    override fun resetOrDeleteProfile(){
        with(profileVault) {
            deleteObject(PRIVKEY_TAG)
            deleteObject(PUBKEY_TAG)
            deleteObject(USERNAME_TAG)
            deleteObject(USER_BIO_TAG)
            deleteObject(PROFILE_IMAGE_TAG)

        }
    }

    override fun profileExists(profile: Profile?): Boolean {
        return profileDataExists()
    }

    private fun profileDataExists(): Boolean {
        val containsEditableProperties = profileVault.existsObject(USERNAME_TAG)
                                            || profileVault.existsObject(USER_BIO_TAG)
                                            || profileVault.existsObject(PROFILE_IMAGE_TAG)
        return containsEditableProperties && containsIdentityData()
    }

    fun containsIdentityData(): Boolean {
        return profileVault.existsObject(PRIVKEY_TAG) && profileVault.existsObject(PUBKEY_TAG)
    }

}

object LoggedInProfileProvider {
    fun getLoggedProfile(context: Context): Profile {
        val vault = KVault(context.applicationContext, PROFILE_DATA)
        with(vault) {
            val privKey = string(PRIVKEY_TAG) ?: ""
            val pubKey = string(PUBKEY_TAG) ?: ""
            val username = string(USERNAME_TAG) ?: ""
            val profileBio = string(USER_BIO_TAG) ?: ""
            val profileImage = string(PROFILE_IMAGE_TAG) ?: ""
            return Profile(pubKey, privKey, username, profileImage, profileBio)
        }
    }
}

