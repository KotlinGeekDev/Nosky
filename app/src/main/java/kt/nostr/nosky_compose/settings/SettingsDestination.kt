package kt.nostr.nosky_compose.settings

sealed class SettingsDestination {
    object MainSettings: SettingsDestination()
    object AppInformation: SettingsDestination()
    object RelayManagement: SettingsDestination()
}
