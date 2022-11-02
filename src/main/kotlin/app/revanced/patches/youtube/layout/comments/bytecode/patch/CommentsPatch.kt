package app.revanced.patches.youtube.layout.comments.bytecode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.ad.general.bytecode.extensions.MethodExtensions.findMutableMethodOf
import app.revanced.patches.youtube.layout.comments.annotations.CommentsCompatibility
import app.revanced.patches.youtube.layout.comments.bytecode.fingerprints.*
import app.revanced.patches.youtube.layout.comments.resource.patch.CommentsResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c
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
        LiveChatFullscreenResourceFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        // hide shorts comments button
        val shortsCommentsButtonResult = ShortsCommentsButtonFingerprint.result!!
        val shortsCommentsButtonMethod = shortsCommentsButtonResult.mutableMethod

        val checkCastAnchorIndex = shortsCommentsButtonResult.scanResult.patternScanResult!!.endIndex

        shortsCommentsButtonMethod.addInstructions(
            checkCastAnchorIndex + 1, """
                invoke-static {v${(getInstructionByIndex(shortsCommentsButtonMethod, checkCastAnchorIndex) as OneRegisterInstruction).registerA}}, Lapp/revanced/integrations/patches/HideShortsCommentsButtonPatch;->hideShortsCommentsButton(Landroid/view/View;)V
            """
        )

        // hide fullscreen live chat button
        val liveChatFullscreenResourceMethod = LiveChatFullscreenResourceFingerprint.result!!.mutableMethod

        val constIndex = liveChatFullscreenResourceMethod.implementation?.instructions?.indexOfFirst {
            it.opcode.ordinal == Opcode.CONST.ordinal &&
            (it as WideLiteralInstruction).wideLiteral == CommentsResourcePatch.liveChatButtonId
        }!!

        liveChatFullscreenResourceMethod.addInstruction(
            constIndex + 1, """
                sput v${(getInstructionByIndex(liveChatFullscreenResourceMethod, constIndex) as OneRegisterInstruction).registerA}, Lapp/revanced/integrations/patches/HideLiveChatFullScreenButtonPatch;->fullScreenLiveChatButtonId:I
            """
        )

        with (getInstructionByIndex(liveChatFullscreenResourceMethod,constIndex - 1)) {
            if (opcode.ordinal == Opcode.NEW_INSTANCE.ordinal) {
                val liveChatFullscreenVisibilityClass =
                    context.findClass((this as BuilderInstruction21c).reference.toString())!!.mutableClass

                for (method in liveChatFullscreenVisibilityClass.methods) {
                    with (liveChatFullscreenVisibilityClass.findMutableMethodOf(method)) {
                        var jumpInstruction = true

                        implementation!!.instructions.forEachIndexed { compiledInstructionIndex, compiledInstruction ->
                            if (compiledInstruction.opcode.ordinal == Opcode.INVOKE_VIRTUAL.ordinal) {
                                val definedInstruction = (compiledInstruction as? BuilderInstruction35c)

                                if (definedInstruction?.reference.toString() ==
                                    "Landroid/view/View;->setVisibility(I)V") {

                                    jumpInstruction = !jumpInstruction
                                    if (jumpInstruction) return@forEachIndexed

                                    val firstRegister = definedInstruction?.registerC
                                    val secondRegister = definedInstruction?.registerD

                                    addInstructions(
                                        compiledInstructionIndex, """
                                            invoke-static {v$firstRegister, v$secondRegister}, Lapp/revanced/integrations/patches/HideLiveChatFullScreenButtonPatch;->hideLiveChatFullScreenButton(Landroid/view/View;I)I
                                            move-result v$secondRegister
                                        """
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        return PatchResultSuccess()
    }

    companion object {
        fun getInstructionByIndex(method: MutableMethod, index: Int) = method.instruction(index)
    }
}
