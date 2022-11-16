package kt.nostr.nosky_compose.profile.model

import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.acinq.secp256k1.Secp256k1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import ktnostr.crypto.toHexString
import java.security.SecureRandom

/**
 * TODO: Add getProfile function which will se savedStateHandle and test it.
 */

@Stable
@Parcelize
data class Profile(
    val pubKey: String = "",
    val privKey: String = "",
    val userName: String = "",
    val profileImage: String = "",
    val bio: String = "",
    val followers: Int = 0,
    val following: Int = 0): Parcelable {
    override fun toString(): String = "Profile($pubKey)"
}

class ProfileViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    //private val cryptoContext = CryptoUtils.get()

//    private val internalPubKey = MutableStateFlow("")
//    val pubKey = internalPubKey.asStateFlow()
//
//    private val internalPrivKey = MutableStateFlow("")
//    val privKey = internalPrivKey.asStateFlow()


    private val internalProfile = MutableStateFlow(Profile())
    val newUserProfile = internalProfile.asStateFlow()

//    val stateProfile = combine(flow = pubKey, flow2 = privKey){ newPub, newPriv ->
//        Profile(pubKey = newPub, privKey = newPriv)
//    }.stateIn(viewModelScope, SharingStarted.Lazily, Profile())


    fun updatePrivKey(newKey: String){

        internalProfile.update { currentProfile -> currentProfile.copy(privKey = newKey) }
        Log.d("NoskyApp", "updatePrivKey -> value: ${newUserProfile.value.privKey} ")
    }

    fun updatePubKey(newKey: String){

        internalProfile.update { currentProfile -> currentProfile.copy(pubKey = newKey) }
        Log.d("NoskyApp", "updatePubKey -> value: ${newUserProfile.value.pubKey} ")

    }

    fun updateUserName(updatedName: String){
        internalProfile.update { currentProfile -> currentProfile.copy(userName = updatedName) }
        Log.d("NoskyApp", "updateUserName -> value: ${newUserProfile.value.userName} ")
    }

    fun updateBio(updatedBio: String){
        internalProfile.update { currentProfile -> currentProfile.copy(bio = updatedBio) }
        Log.d("NoskyApp", "updateBio -> value: ${newUserProfile.value.bio} ")
    }

    fun updateProfileImageLink(updatedImageUri: String){
        internalProfile.update { currentProfile ->
            currentProfile.copy(profileImage = updatedImageUri)
        }

        Log.d("NoskyApp","updateProfileImageLink -> value: ${newUserProfile.value.profileImage}")
    }

    fun generateProfile() {
        viewModelScope.launch(Dispatchers.Default) {
            val privKey  = generatePrivKey()
            val pubKey = Secp256k1.get().pubkeyCreate(privKey).drop(1).take(32).toByteArray()
            internalProfile.update { currentProfile ->
                currentProfile.copy(privKey = privKey.toHexString())
            }
            internalProfile.update { profile -> profile.copy(pubKey = pubKey.toHexString()) }
        }

        println("userProfile secKey: ${newUserProfile.value.privKey}")
        println("userProfile pubKey: ${newUserProfile.value.pubKey}")

    }

    //This is here temporarily.
    private fun generatePrivKey(): ByteArray {
        val secretKey = ByteArray(32)
        val pseudoRandomBytes = SecureRandom()
        pseudoRandomBytes.nextBytes(secretKey)
        return secretKey
    }

    fun deleteResetProfile(){
        internalProfile.update { profile -> profile.copy(pubKey = "", privKey = "") }
    }

    fun getLoggedInProfile(): Profile {
        val loggedInProfile = savedStateHandle.getStateFlow("profile_data", Profile())
        return loggedInProfile.value
    }

    override fun onCleared() {
        super.onCleared()
        println("ViewModel cleared.")
        savedStateHandle["profile_data"] = newUserProfile.value
    }


    companion object {
        fun create(): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                val savedStateHandle = createSavedStateHandle()
                ProfileViewModel(savedStateHandle)
            }
        }
    }

}

