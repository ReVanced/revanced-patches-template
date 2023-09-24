package app.revanced.patches.youtube.misc.fix.playback

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.patches.youtube.misc.settings.SettingsResourcePatch

@Patch(dependencies = [SettingsPatch::class, ResourceMappingPatch::class])
object SpoofSignatureResourcePatch : ResourcePatch() {
    internal var scrubbedPreviewThumbnailResourceId: Long = -1

    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            PreferenceScreen(
                "revanced_spoof_signature_verification_screen",
                "revanced_spoof_signature_verification_screen_title",
                listOf(
                    SwitchPreference(
                        "revanced_spoof_signature_verification_enabled",
                        "revanced_spoof_signature_verification_enabled_title",
                        "revanced_spoof_signature_verification_enabled_summary_on",
                        "revanced_spoof_signature_verification_enabled_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_spoof_signature_in_feed_enabled",
                        "revanced_spoof_signature_in_feed_enabled_title",
                        "revanced_spoof_signature_in_feed_enabled_summary_on",
                        "revanced_spoof_signature_in_feed_enabled_summary_off",
                    ),
                )
            )
        )
        SettingsResourcePatch.mergePatchStrings("SpoofSignatureVerification")

        scrubbedPreviewThumbnailResourceId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "thumbnail"
        }.id
    }
}
