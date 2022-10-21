package kt.nostr.nosky_compose.profile.model

import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.acinq.secp256k1.Secp256k1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kt.nostr.nosky_compose.NoskyApplication
import ktnostr.crypto.toHexString
import java.security.SecureRandom

/**
 * TODO:
 *  - Add important pieces of info to complete Profile model
 *    (username/displayName, imageUrl, bio, following, followers).
 */

@Stable
@Parcelize
data class Profile(
    var pubKey: String = "",
    var privKey: String = "",
    var userName: String = "",
    var profileImage: String = "",
    var bio: String = ""): Parcelable {
    override fun toString(): String = "Profile($pubKey)"
}

class ProfileViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val profile: Profile = Profile()
): ViewModel() {

    //private val cryptoContext = CryptoUtils.get()

    private val internalPubKey = MutableStateFlow("")
    val pubKey = internalPubKey.asStateFlow()

    private val internalPrivKey = MutableStateFlow("")
    val privKey = internalPrivKey.asStateFlow()


    private val internalProfile = MutableStateFlow(Profile())
    val newUserProfile = internalProfile.asStateFlow()


    fun updatePrivKey(newKey: String){

        internalPrivKey.value = newKey
        profile.privKey = privKey.value
        Log.d("NoskyApp", "updatePrivKey -> Privkey value: ${profile.privKey} ")
    }

    fun updatePubKey(newKey: String){

        internalPubKey.value = newKey
        profile.pubKey = pubKey.value
        Log.d("NoskyApp", "updatePubKey -> Pubkey value: ${profile.pubKey} ")

    }

    fun updateUserName(updatedName: String){
        internalProfile.update { currentProfile -> currentProfile.copy(userName = updatedName) }
        profile.userName = newUserProfile.value.userName
        Log.d("NoskyApp", "updateUserName -> Username value: ${profile.userName} ")
    }

    fun updateBio(updatedBio: String){
        internalProfile.update { currentProfile -> currentProfile.copy(bio = updatedBio) }
        profile.bio = newUserProfile.value.bio
        Log.d("NoskyApp", "updateBio -> Bio value: ${profile.bio} ")
    }

    fun updateProfileImageLink(updatedImageUri: String){
        internalProfile.update { currentProfile -> currentProfile.copy(profileImage = updatedImageUri) }
        profile.profileImage = internalProfile.value.profileImage
        Log.d("NoskyApp", "updateProfileImageLink: ImageLink value: ${profile.profileImage}")
    }

    fun generateProfile() {
        viewModelScope.launch(Dispatchers.Default) {
            val privKey  = generatePrivKey()
            val pubKey = Secp256k1.get().pubkeyCreate(privKey).drop(1).take(32).toByteArray()
            internalProfile.update { currentProfile -> currentProfile.copy(privKey = privKey.toHexString()) }
            internalProfile.update { profile -> profile.copy(pubKey = pubKey.toHexString()) }
            profile.privKey = privKey.toHexString()
            profile.pubKey = pubKey.toHexString()
        }

        println("internalProfile secKey: ${internalProfile.value.privKey}")
        println("internalProfile pubKey: ${internalProfile.value.pubKey}")
        println("profile pubKey: ${profile.pubKey}")
    }

    //This is here temporarily.
    private fun generatePrivKey(): ByteArray {
        val secretKey = ByteArray(32)
        val pseudoRandomBytes = SecureRandom()
        pseudoRandomBytes.nextBytes(secretKey)
        return secretKey
    }

    fun deleteResetProfile(){
        profile.privKey = ""
        profile.pubKey = ""
    }

//    fun obtainProfile(): Profile {
//        return savedStateHandle.getStateFlow("profile_data", profile).value
//    }

    override fun onCleared() {
        super.onCleared()
        println("ViewModel cleared.")
        savedStateHandle["profile_data"] = profile
    }


    companion object {
        fun create(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val appReference = (this[APPLICATION_KEY] as NoskyApplication)
                val profile = appReference.userProfile

                ProfileViewModel(savedStateHandle, profile)
            }
        }
    }

}

