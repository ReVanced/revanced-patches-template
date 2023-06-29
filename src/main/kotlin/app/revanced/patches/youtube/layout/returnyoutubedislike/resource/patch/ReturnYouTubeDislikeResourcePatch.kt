package app.revanced.patches.youtube.layout.returnyoutubedislike.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.Preference
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.youtube.layout.returnyoutubedislike.annotations.ReturnYouTubeDislikeCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.mergeStrings

@DependsOn([SettingsPatch::class])
@Name("return-youtube-dislike-resource-patch")
@Description("Adds the preferences for Return YouTube Dislike.")
@ReturnYouTubeDislikeCompatibility
@Version("0.0.1")
class ReturnYouTubeDislikeResourcePatch : ResourcePatch {
    companion object {
        internal var oldUIDislikeId: Long = -1
    }

    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.addPreference(
            Preference(
                StringResource("revanced_ryd_settings_title", "Return YouTube Dislike"),
                StringResource("revanced_ryd_settings_summary", "Settings for Return YouTube Dislike"),
                SettingsPatch.createReVancedSettingsIntent("ryd_settings")
            )
        )
        // merge strings
        context.mergeStrings("returnyoutubedislike/host/values/strings.xml")

        oldUIDislikeId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "dislike_button"
        }.id

        return PatchResultSuccess()
    }
}