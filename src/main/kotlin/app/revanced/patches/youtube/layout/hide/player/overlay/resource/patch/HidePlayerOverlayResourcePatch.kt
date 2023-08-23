package app.revanced.patches.youtube.layout.hide.player.overlay.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@DependsOn([YouTubeSettingsPatch::class, ResourceMappingPatch::class])
class HidePlayerOverlayResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_player_overlay",
                "revanced_hide_player_overlay_title",
                "revanced_hide_player_overlay_summary_on",
                "revanced_hide_player_overlay_summary_off"
            )
        )

        scrimOverlayId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "scrim_overlay"
        }.id
    }

    internal companion object {
        var scrimOverlayId: Long = -1
    }
}