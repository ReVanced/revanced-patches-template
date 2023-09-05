package app.revanced.patches.youtube.misc.fix.playback.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@DependsOn([SettingsPatch::class, ResourceMappingPatch::class])
class SpoofSignatureVerificationResourcePatch : ResourcePatch {

    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_spoof_signature_verification",
                "revanced_spoof_signature_verification_title",
                "revanced_spoof_signature_verification_summary_on",
                "revanced_spoof_signature_verification_summary_off",
            )
        )

        scrubbedPreviewThumbnailResourceId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "thumbnail"
        }.id
    }

    companion object {
        var scrubbedPreviewThumbnailResourceId: Long = -1
    }
}
