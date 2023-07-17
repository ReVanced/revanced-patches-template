package app.revanced.patches.youtube.layout.hide.player.overlay.resource.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.resourceIdOf

@DependsOn([SettingsPatch::class])
class HidePlayerOverlayResourcePatch : ResourcePatch {
    override suspend fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_player_overlay",
                StringResource("revanced_hide_player_overlay_title", "Hide background overlay in player"),
                StringResource("revanced_hide_player_overlay_summary_on", "Background overlay is hidden"),
                StringResource("revanced_hide_player_overlay_summary_off", "Background overlay is shown")
            )
        )

        scrimOverlayId = context.resourceIdOf("id", "scrim_overlay")
    }

    internal companion object {
        var scrimOverlayId: Long = -1
    }
}