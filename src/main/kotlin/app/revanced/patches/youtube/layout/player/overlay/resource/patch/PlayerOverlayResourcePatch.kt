package app.revanced.patches.youtube.layout.player.overlay.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.InputType
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.shared.settings.preference.impl.TextPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@DependsOn([SettingsPatch::class, ResourceMappingPatch::class])
class PlayerOverlayResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            PreferenceScreen(
                "revanced_player_overlay",
                StringResource("revanced_player_overlay_title", "Player background overlay"),
                listOf(
                    SwitchPreference(
                        "revanced_hide_player_overlay",
                        StringResource("revanced_hide_player_overlay_title", "Hide background overlay in player"),
                        StringResource("revanced_hide_player_overlay_summary_on", "Background overlay is hidden"),
                        StringResource("revanced_hide_player_overlay_summary_off", "Background overlay is shown")
                    ),
                    TextPreference(
                        "revanced_player_overlay_opacity_value",
                        StringResource("revanced_player_overlay_opacity_value_title", "Change overlay opacity"),
                        StringResource("revanced_player_overlay_opacity_value_summary", "Enter opacity value from 0-100, where 0 is transparent"),
                        InputType.NUMBER
                    )
                ),
                StringResource("revanced_player_overlay_summary", "Player background overlay settings")
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
