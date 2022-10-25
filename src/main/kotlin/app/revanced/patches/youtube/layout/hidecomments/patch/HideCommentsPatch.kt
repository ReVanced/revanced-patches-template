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
import app.revanced.patches.youtube.layout.buttons.annotations.HideCommentsCompatibility
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Patch
@DependsOn([ResourceMappingResourcePatch::class, GeneralBytecodeAdsPatch::class])
@Name("hide-comments")
@Description("Hides comments section below the video player.")
@HideCommentsCompatibility
@Version("0.0.1")
class HideCommentsPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        SwitchPreference(
            "revanced_hide_comments_section",
            StringResource("revanced_hide_comments_section_title", "Remove comments section"),
            false,
            StringResource("revanced_hide_comments_section_summary_on", "Comment section is hidden"),
            StringResource("revanced_hide_comments_section_summary_off", "Comment section is shown")
        ),
        return PatchResultSuccess()
    }
}
