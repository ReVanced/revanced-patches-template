package app.revanced.patches.youtube.layout.hidepreviewcomment.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.ad.general.bytecode.patch.GeneralBytecodeAdsPatch
import app.revanced.patches.youtube.layout.buttons.annotations.HidePreviewCommentCompatibility
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Patch
@DependsOn([ResourceMappingResourcePatch::class, GeneralBytecodeAdsPatch::class])
@Name("hide-preview-comment")
@Description("Hides preview comment below the video player.")
@HidePreviewCommentCompatibility
@Version("0.0.1")
class HidePreviewCommentPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_preview_comment",
                StringResource("revanced_hide_preview_comment_title", "Hide preview comment"),
                false,
                StringResource("revanced_hide_preview_comment_on", "Preview comment is hidden"),
                StringResource("revanced_hide_preview_comment_off", "Preview comment is shown")
            ),
        )
        return PatchResultSuccess()
    }
}
