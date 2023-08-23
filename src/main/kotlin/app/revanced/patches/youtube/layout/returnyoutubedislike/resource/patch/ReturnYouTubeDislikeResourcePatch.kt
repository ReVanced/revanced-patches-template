package app.revanced.patches.youtube.layout.returnyoutubedislike.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.Preference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@DependsOn([YouTubeSettingsPatch::class])
class ReturnYouTubeDislikeResourcePatch : ResourcePatch {
    companion object {
        internal var oldUIDislikeId: Long = -1
    }

    override fun execute(context: ResourceContext) {
        YouTubeSettingsPatch.addPreference(
            Preference(
                "revanced_ryd_settings_title",
                "revanced_ryd_settings_summary",
                YouTubeSettingsPatch.createReVancedSettingsIntent("ryd_settings_intent")
            )
        )

        oldUIDislikeId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "dislike_button"
        }.id
    }
}