package app.revanced.patches.youtube.interaction.seekbar.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@DependsOn([YouTubeSettingsPatch::class])
class EnableSeekbarTappingResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        YouTubeSettingsPatch.PreferenceScreen.INTERACTIONS.addPreferences(
            SwitchPreference(
                "revanced_seekbar_tapping",
                "revanced_seekbar_tapping_title",
                "revanced_seekbar_tapping_summary_on",
                "revanced_seekbar_tapping_summary_off"
            )
        )
    }
}