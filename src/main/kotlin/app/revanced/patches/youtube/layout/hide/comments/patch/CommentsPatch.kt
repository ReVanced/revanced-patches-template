package app.revanced.patches.youtube.layout.hide.comments.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.comments.annotations.HideCommentsCompatibility
import app.revanced.patches.youtube.misc.litho.filter.patch.LithoFilterPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@Name("comments")
@Description("Hides components related to comments.")
@HideCommentsCompatibility
@DependsOn([YouTubeSettingsPatch::class, LithoFilterPatch::class])
@Version("0.0.1")
class CommentsPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        LithoFilterPatch.addFilter(FILTER_CLASS_DESCRIPTOR)

        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            PreferenceScreen(
                "revanced_comments_preference_screen",
                "revanced_comments_preference_screen_title",
                listOf(
                    SwitchPreference(
                        "revanced_hide_comments_section",
                        "revanced_hide_comments_section_title",
                        "revanced_hide_comments_section_summary_on",
                        "revanced_hide_comments_section_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_hide_preview_comment",
                        "revanced_hide_preview_comment_title",
                        "revanced_hide_preview_comment_on",
                        "revanced_hide_preview_comment_off"
                    )
                ),
                "revanced_comments_preference_screen_summary"
            )
        )

        return PatchResultSuccess()
    }

    private companion object {
        const val FILTER_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/components/CommentsFilter;"
    }
}
