package kt.nostr.nosky_compose.profile.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.parcelize.Parcelize
import kt.nostr.nosky_compose.NoskyApplication
import kotlin.reflect.KProperty

/**
 * TODO:
 *  - Add important pieces of info to complete Profile model
 *    (username/displayName, imageUrl, bio, following, followers).
 */

@Stable
@Parcelize
class Profile(var pubKey: String = "", var privKey: String = ""): Parcelable {
    override fun toString(): String = "Profile($pubKey)"
}

class ProfileViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val profile: Profile = Profile()
): ViewModel() {


    private var internalPubKey = MutableStateFlow("")
    val pubKey = internalPubKey.asStateFlow()

    private var internalPrivKey = MutableStateFlow("")
    val privKey = internalPrivKey.asStateFlow()


    fun updatePrivKey(newKey: String){

        internalPrivKey.tryEmit(newKey)
        profile.privKey = privKey.value
        println("Privkey value: ${profile.privKey}")
    }

    fun updatePubKey(newKey: String){

        internalPubKey.tryEmit(newKey)
        profile.pubKey = pubKey.value
        println("Privkey value: ${profile.pubKey}")

    }

    fun deleteResetProfile(){
        profile.privKey = ""
        profile.pubKey = ""
    }

    fun obtainProfile(): Profile {
        return savedStateHandle.getStateFlow("profile_data", profile).value
    }

    override fun onCleared() {
        super.onCleared()
        println("ViewModel cleared.")
        savedStateHandle["profile_data"] = profile
    }

    operator fun getValue(profile: Nothing?, property: KProperty<*>): ProfileViewModel {
        return this
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

