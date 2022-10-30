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
        val liveChatFullscreenButtonMethod = LiveChatFullscreenButtonFingerprint.result!!.mutableMethod
        val liveChatButtonIndex = liveChatFullscreenButtonMethod.implementation?.instructions?.indexOfFirst {
            it.opcode.ordinal == Opcode.CONST.ordinal &&
            (it as WideLiteralInstruction).wideLiteral == CommentsResourcePatch.liveChatButtonId
        }!!

        liveChatFullscreenButtonMethod.addInstruction(
            liveChatButtonIndex + 1, """
                sput v${(liveChatFullscreenButtonMethod.instruction(liveChatButtonIndex) as OneRegisterInstruction).registerA}, Lapp/revanced/integrations/patches/HideLiveChatFullScreenButtonPatch;->fullScreenLiveChatButtonId:I
            """
        )

        val setVisibilityAnchorFingerprints = listOf(
            object : MethodFingerprint(
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
            ) {},
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
        setVisibilityAnchorFingerprints.resolve(
            context,
            listOf(LiveChatFullscreenButtonVisibilityFingerprint.result!!.classDef)
        )

        for (i in 0..setVisibilityAnchorFingerprints.size) {
            with (setVisibilityAnchorFingerprints[i].result!!) {
                val setVisibilityAnchorMethod = mutableMethod

                fun visibilityPatchBuilder(viewRegister: Int, visibilityRegister: Int) = """
                    invoke-static {v$viewRegister, v$visibilityRegister}, Lapp/revanced/integrations/patches/HideLiveChatFullScreenButtonPatch;->hideLiveChatFullScreenButton(Landroid/view/View;I)I
                    move-result v$visibilityRegister
                """

                val invokeVirtualEndIndex =
                    scanResult.patternScanResult!!.endIndex
                val invokeVirtualEndInstruction = (setVisibilityAnchorMethod.instruction(
                    invokeVirtualEndIndex) as BuilderInstruction35c
                )
                setVisibilityAnchorMethod.addInstructions(
                    invokeVirtualEndIndex,
                    visibilityPatchBuilder(
                        invokeVirtualEndInstruction.registerC,
                        invokeVirtualEndInstruction.registerD
                    )
                )

                if (i == 0) {
                    val invokeVirtualStartIndex = scanResult.patternScanResult!!.startIndex
                    val invokeVirtualStartInstruction = (setVisibilityAnchorMethod.instruction(
                        invokeVirtualStartIndex) as BuilderInstruction35c
                    )
                    setVisibilityAnchorMethod.addInstructions(
                        invokeVirtualStartIndex,
                        visibilityPatchBuilder(
                            invokeVirtualStartInstruction.registerC,
                            invokeVirtualStartInstruction.registerD
                        )
                    )
                }
            }
        }

        return PatchResultSuccess()
    }
}
