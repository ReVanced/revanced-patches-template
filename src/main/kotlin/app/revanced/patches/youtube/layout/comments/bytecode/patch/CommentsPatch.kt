package app.revanced.patches.youtube.layout.comments.bytecode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.comments.annotations.CommentsCompatibility
import app.revanced.patches.youtube.layout.comments.bytecode.fingerprints.LiveChatFullscreenButtonFingerprint
import app.revanced.patches.youtube.layout.comments.bytecode.fingerprints.LiveChatFullscreenButtonVisibilityFingerprint
import app.revanced.patches.youtube.layout.comments.bytecode.fingerprints.ShortsCommentsButtonFingerprint
import app.revanced.patches.youtube.layout.comments.resource.patch.CommentsResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Patch
@DependsOn([IntegrationsPatch::class, CommentsResourcePatch::class])
@Name("comments")
@Description("Hides components related to comments.")
@CommentsCompatibility
@Version("0.0.1")
class CommentsPatch : BytecodePatch(
    listOf(
        ShortsCommentsButtonFingerprint,
        LiveChatFullscreenButtonFingerprint,
        LiveChatFullscreenButtonVisibilityFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
    // hide shorts comments button
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

    // hide fullscreen live chat button
        // save the wide literal resource value
        val liveChatFullscreenButtonMethod = LiveChatFullscreenButtonFingerprint.result!!.mutableMethod
        val liveChatButtonIndex = liveChatFullscreenButtonMethod.implementation?.instructions?.indexOfFirst {
            it.opcode.ordinal == Opcode.CONST.ordinal && (it as WideLiteralInstruction).wideLiteral == CommentsResourcePatch.liveChatButtonId
        }!!

        liveChatFullscreenButtonMethod.addInstruction(
            liveChatButtonIndex + 1, """
                sput v${(liveChatFullscreenButtonMethod.instruction(liveChatButtonIndex) as OneRegisterInstruction).registerA}, Lapp/revanced/integrations/patches/HideLiveChatFullScreenButtonPatch;->fullScreenLiveChatButtonId:I
            """
        )

        // apply the patch to hide the view, whose id is equal to the previous wide literal value
        // total setVisibilityInstructions to patch inside the class = 4
        fun visibilityPatchBuilder(viewRegister: Int, visibilityRegister: Int) = """
            invoke-static {v$viewRegister, v$visibilityRegister}, Lapp/revanced/integrations/patches/HideLiveChatFullScreenButtonPatch;->hideLiveChatFullScreenButton(Landroid/view/View;I)I
            move-result v$visibilityRegister
        """

        val liveChatFullScreenButtonVisibilityResult = LiveChatFullscreenButtonVisibilityFingerprint.result!!
        val liveChatFullScreenButtonVisibilityMethod = liveChatFullScreenButtonVisibilityResult.mutableMethod
        val setVisibilityValueAnchorFingerprint = object : MethodFingerprint(
            opcodes = listOf(
                Opcode.INVOKE_VIRTUAL,
                Opcode.CONST_4,
                Opcode.INVOKE_DIRECT,
                Opcode.IGET_OBJECT,
                Opcode.IGET_OBJECT,
                Opcode.NEW_INSTANCE,
                Opcode.INVOKE_DIRECT,
                Opcode.INVOKE_INTERFACE,
                Opcode.RETURN_VOID,
                Opcode.IGET_WIDE,
                Opcode.IGET_OBJECT,
                Opcode.INVOKE_VIRTUAL,
            )
        ) {}
        val setVisibilityValueAnchorScan = setVisibilityValueAnchorFingerprint.also {
            it.resolve(context, liveChatFullScreenButtonVisibilityMethod, liveChatFullScreenButtonVisibilityResult.classDef)
        }.result!!.scanResult.patternScanResult!!

        for (i in 0..1) {
            val invokeVirtualIndex =
                if (i == 0) setVisibilityValueAnchorScan.endIndex
                else setVisibilityValueAnchorScan.startIndex
            val invokeVirtualInstruction =
                (liveChatFullScreenButtonVisibilityMethod.instruction(invokeVirtualIndex) as BuilderInstruction35c)

            liveChatFullScreenButtonVisibilityMethod.addInstructions(
                invokeVirtualIndex,
                visibilityPatchBuilder(
                    invokeVirtualInstruction.registerC,
                    invokeVirtualInstruction.registerD
                )
            )
        }

        val SetVisibilityAnchorFingerprints = listOf(
            object : MethodFingerprint(
                "V", AccessFlags.PUBLIC or AccessFlags.FINAL, null, listOf(
                    Opcode.IGET_OBJECT,
                    Opcode.IGET,
                    Opcode.INVOKE_VIRTUAL,
                )
            ) {},
            object : MethodFingerprint(
                "V", AccessFlags.PUBLIC or AccessFlags.FINAL, null, listOf(
                    Opcode.IGET_OBJECT,
                    Opcode.CONST_4,
                    Opcode.INVOKE_VIRTUAL,
                )
            ) {}
        )
        SetVisibilityAnchorFingerprints.resolve(
            context,
            listOf(liveChatFullScreenButtonVisibilityResult.classDef)
        )

        for (SetVisibilityAnchorFingerprint in SetVisibilityAnchorFingerprints) {
            val setVisibilityAnchorResult = SetVisibilityAnchorFingerprint.result!!
            val setVisibilityAnchorMethod = setVisibilityAnchorResult.mutableMethod
            val setVisibilityAnchorIndex = setVisibilityAnchorResult.scanResult.patternScanResult!!.endIndex
            val invokeVirtualInstruction = (setVisibilityAnchorMethod.instruction(setVisibilityAnchorIndex) as BuilderInstruction35c)

            setVisibilityAnchorMethod.addInstructions(
                setVisibilityAnchorIndex,
                visibilityPatchBuilder(
                    invokeVirtualInstruction.registerC,
                    invokeVirtualInstruction.registerD
                )
            )
        }

        return PatchResultSuccess()
    }
}
