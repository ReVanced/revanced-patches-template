package app.revanced.patches.youtube.misc.fix.playback

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.SettingsPatch

@Patch(dependencies = [SettingsPatch::class, ResourceMappingPatch::class])
object SpoofSignatureVerificationResourcePatch : ResourcePatch() {
    internal var scrubbedPreviewThumbnailResourceId: Long = -1

    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            PreferenceScreen(
                key = "revanced_spoof_signature_verification",
                title = StringResource(
                    "revanced_spoof_signature_verification_title",
                    "Spoof app signature"
                ),
                preferences = listOf(
                    SwitchPreference(
                        "revanced_spoof_signature_verification_enabled",
                        StringResource("revanced_spoof_signature_verification_enabled_title", "Spoof app signature"),
                        StringResource(
                            "revanced_spoof_signature_verification_enabled_summary_on",
                            "App signature spoofed\\n\\n"
                                    + "Side effects include:\\n"
                                    + "• No ambient mode\\n"
                                    + "• Videos can't be downloaded\\n"
                                    + "• Seekbar thumbnails not showing up"
                        ),
                        StringResource(
                            "revanced_spoof_signature_verification_enabled_summary_off",
                            "App signature not spoofed\\n\\nVideo playback may not work"
                        ),
                        StringResource(
                            "revanced_spoof_signature_verification_enabled_user_dialog_message",
                            "Turning off this setting will cause video playback issues."
                        )
                    ),
                    SwitchPreference(
                        "revanced_spoof_signature_in_feed_enabled",
                        StringResource("revanced_spoof_signature_in_feed_enabled_title", "Spoof app signature in feed"),
                        StringResource(
                            "revanced_spoof_signature_in_feed_enabled_summary_on",
                            "App signature spoofed\\n\\n"
                                    + "Automatically played videos in feed will forcibly be recorded in watch history"
                        ),
                        StringResource(
                            "revanced_spoof_signature_in_feed_enabled_summary_off",
                            "App signature not spoofed\\n\\nVideo playback in feed may not work"
                        ),
                        StringResource(
                            "revanced_spoof_signature_in_feed_enabled_user_dialog_message",
                            "Turning off this setting will cause video playback issues."
                        )
                    )
                )
            )
        )

        scrubbedPreviewThumbnailResourceId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "thumbnail"
        }.id
    }
}
