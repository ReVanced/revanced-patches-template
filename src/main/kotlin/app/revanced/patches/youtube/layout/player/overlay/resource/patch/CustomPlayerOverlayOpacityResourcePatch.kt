package app.revanced.patches.youtube.layout.player.overlay.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.InputType
import app.revanced.patches.shared.settings.preference.impl.TextPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@DependsOn([YouTubeSettingsPatch::class, ResourceMappingPatch::class])
class CustomPlayerOverlayOpacityResourcePatch : ResourcePatch {

    override fun execute(context: ResourceContext) {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            TextPreference(
                "revanced_player_overlay_opacity",
                "revanced_player_overlay_opacity_title",
                "revanced_player_overlay_opacity_summary",
                InputType.NUMBER
            )
        )

        scrimOverlayId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "scrim_overlay"
        }.id
    }

    internal companion object {
        var scrimOverlayId = -1L
    }
}
