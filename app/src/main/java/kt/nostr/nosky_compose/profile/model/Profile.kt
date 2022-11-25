package kt.nostr.nosky_compose.profile.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
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
import kt.nostr.nosky_compose.profile.ProfileDataStore
import kt.nostr.nosky_compose.profile.ProfileStore
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
    override fun toString(): String = "Profile(pubkey=$pubKey, username=$userName, bio=$bio)"
}

class ProfileViewModel(
    private val profileStore: ProfileStore
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
    }

    fun updatePubKey(newKey: String){

        internalProfile.update { currentProfile -> currentProfile.copy(pubKey = newKey) }

    }

    fun updateUserName(updatedName: String){
        internalProfile.update { currentProfile -> currentProfile.copy(userName = updatedName) }
    }

    fun updateBio(updatedBio: String){
        internalProfile.update { currentProfile -> currentProfile.copy(bio = updatedBio) }
    }

    fun updateProfileImageLink(updatedImageUri: String){
        internalProfile.update { currentProfile ->
            currentProfile.copy(profileImage = updatedImageUri)
        }

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


    }

    //This is here temporarily.
    private fun generatePrivKey(): ByteArray {
        val secretKey = ByteArray(32)
        val pseudoRandomBytes = SecureRandom()
        pseudoRandomBytes.nextBytes(secretKey)
        return secretKey
    }

    fun deleteResetProfile(){
        internalProfile.update { profile ->
            profile.copy(
                pubKey = "",
                privKey = "",
                userName = "",
                profileImage = "",
                bio = ""
            )
        }
    }

    fun saveProfile(){
        profileStore.saveProfile(newUserProfile.value)
    }

    fun getLoggedInProfile(): Profile {
        val loggedInProfile = profileStore.getProfile()
        return loggedInProfile
    }

    override fun onCleared() {
        super.onCleared()
        println("ViewModel cleared.")

    }


    companion object {
        fun create(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contextProvider = this[APPLICATION_KEY] as NoskyApplication
                val profileStore = ProfileDataStore(contextProvider.applicationContext)
                ProfileViewModel(profileStore)
            }
        }
    }

}

