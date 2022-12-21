package kt.nostr.nosky_compose.profile.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kt.nostr.nosky_compose.NoskyApplication
import kt.nostr.nosky_compose.home.backend.opsList
import kt.nostr.nosky_compose.profile.LocalProfileDataStore
import kt.nostr.nosky_compose.profile.ProfileProvider
import kt.nostr.nosky_compose.utility_functions.misc.toHexString
import ktnostr.crypto.CryptoUtils
import nostr.postr.Bech32
import nostr.postr.toHex


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

class LocalProfileViewModel(
    private val profileStore: ProfileProvider
): ViewModel() {


//    private val internalPubKey = MutableStateFlow("")
//    val pubKey = internalPubKey.asStateFlow()
//
//    private val internalPrivKey = MutableStateFlow("")
//    val privKey = internalPrivKey.asStateFlow()

    private val internalProfile = MutableStateFlow(Profile())
    val newUserProfile = internalProfile.asStateFlow()

    private val _profilePosts:MutableStateFlow<PostsResult> = MutableStateFlow(PostsResult.Loading)
    val profilePosts = _profilePosts.asStateFlow()

//    val stateProfile = combine(flow = pubKey, flow2 = privKey){ newPub, newPriv ->
//        Profile(pubKey = newPub, privKey = newPriv)
//    }.stateIn(viewModelScope, SharingStarted.Lazily, Profile())
    init {
        viewModelScope.launch {
            _profilePosts.update { PostsResult.Loading }
            delay(3000)
            _profilePosts.update { PostsResult.Success(opsList) }
        }
    }


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

            val privKey = CryptoUtils.generatePrivateKey()
            val pubKey = CryptoUtils.getPublicKey(privKey)
            internalProfile.update { currentProfile ->
                currentProfile.copy(privKey = privKey.toHexString())
            }
            internalProfile.update { profile -> profile.copy(pubKey = pubKey.toHexString()) }
        }


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
        val tempSecKey = newUserProfile.value.privKey
        val tempPubKey = newUserProfile.value.pubKey
        if (tempPubKey.startsWith("npub1")){
            val (_, keyBytes, _) = Bech32.decode(tempPubKey, noChecksum = true)
            internalProfile.update {
                it.copy(
                    pubKey = keyBytes.toByteArray().toHex()
                )
            }
        }
        if (tempSecKey.startsWith("nsec1")){
            val (_, keyBytes, _) = Bech32.decode(tempSecKey, noChecksum = true)
            internalProfile.update {
                it.copy(
                    privKey = keyBytes.toByteArray().toHex()
                )
            }
        }

        profileStore.saveProfile(newUserProfile.value)
        deleteResetProfile()
    }

    private fun getLoggedInProfile(): Profile {
        val loggedInProfile = profileStore.getProfile()
        return loggedInProfile
    }

    fun updateProfile(){
       val loggedProfile = getLoggedInProfile()
        internalProfile.update { profile ->
            with(loggedProfile){
                profile.copy(
                    pubKey = pubKey,
                    privKey = privKey,
                    userName = userName,
                    profileImage = profileImage,
                    bio = bio
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("ViewModel cleared.")

    }


    companion object {
        fun create(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contextProvider = this[APPLICATION_KEY] as NoskyApplication
                val profileStore = LocalProfileDataStore(contextProvider.applicationContext)
                LocalProfileViewModel(profileStore)
            }
        }
    }

}

