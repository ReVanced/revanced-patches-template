package app.revanced.patches.youtube.layout.comments.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.ad.general.bytecode.patch.GeneralBytecodeAdsPatch
import app.revanced.patches.youtube.layout.comments.annotations.CommentsCompatibility
import app.revanced.patches.youtube.layout.comments.fingerprints.ShortsCommentsButtonFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class, ResourceMappingResourcePatch::class, GeneralBytecodeAdsPatch::class])
@Name("comments")
@Description("Hides comments components below the video player.")
@CommentsCompatibility
@Version("0.0.1")
class CommentsPatch : BytecodePatch(
    listOf(
        ShortsCommentsButtonFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            PreferenceScreen(
                "revanced_comments",
                StringResource("revanced_comments_title", "Comments"),
                listOf(
                    SwitchPreference(
                        "revanced_hide_comments_section",
                        StringResource("revanced_hide_comments_section_title", "Remove comments section"),
                        false,
                        StringResource("revanced_hide_comments_section_summary_on", "Comment section is hidden"),
                        StringResource("revanced_hide_comments_section_summary_off", "Comment section is shown")
                    ),
                    SwitchPreference(
                        "revanced_hide_preview_comment",
                        StringResource("revanced_hide_preview_comment_title", "Hide preview comment"),
                        false,
                        StringResource("revanced_hide_preview_comment_on", "Preview comment is hidden"),
                        StringResource("revanced_hide_preview_comment_off", "Preview comment is shown")
                    ),
                    SwitchPreference(
                        "revanced_hide_shorts_comments_button",
                        StringResource("revanced_hide_shorts_comments_button_title", "Hide shorts comments button"),
                        false,
                        StringResource("revanced_hide_shorts_comments_button_on", "Shorts comments button is hidden"),
                        StringResource("revanced_hide_shorts_comments_button_off", "Shorts comments button is shown")
                    ),
                ),
                StringResource("revanced_comments_summary", "Manage the visibility of comments section components")
            )
        )

        val shortsCommentsButtonResult = ShortsCommentsButtonFingerprint.result!!
        val shortsCommentsButtonMethod = shortsCommentsButtonResult.mutableMethod

        val checkCastAnchorFingerprint = object : MethodFingerprint(
            opcodes = listOf(
                Opcode.CONST,
                Opcode.CONST_HIGH16,
                Opcode.IF_EQZ,
                Opcode.CONST,
                Opcode.INVOKE_STATIC,
                Opcode.MOVE_RESULT_OBJECT,
                Opcode.CHECK_CAST,
            )
        ) {}
        val checkCastAnchorIndex = checkCastAnchorFingerprint.also {
            it.resolve(context, shortsCommentsButtonMethod, shortsCommentsButtonResult.classDef)
        }.result!!.scanResult.patternScanResult!!.endIndex

        shortsCommentsButtonMethod.addInstructions(
            checkCastAnchorIndex + 1, """
                invoke-static {v${(shortsCommentsButtonMethod.instruction(checkCastAnchorIndex) as OneRegisterInstruction).registerA}}, Lapp/revanced/integrations/patches/HideShortsCommentsButtonPatch;->hideShortsCommentsButton(Landroid/view/View;)V
            """
        )

        return PatchResultSuccess()
    }

    internal companion object {
        internal var shortsCommentsButtonId: Long = ResourceMappingResourcePatch.resourceMappings.single {
            it.type == "drawable" && it.name == "ic_right_comment_32c"
        }.id
    }
}
