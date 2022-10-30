package app.revanced.patches.youtube.layout.comments.bytecode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.layout.comments.annotations.CommentsCompatibility
import app.revanced.patches.youtube.layout.comments.bytecode.fingerprints.*
import app.revanced.patches.youtube.layout.comments.resource.patch.CommentsResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
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
        ShortsCommentsButtonParentFingerprint,
        LiveChatFullscreenButtonFingerprint,
        LiveChatFullscreenButtonVisibilityFingerprint,
        FirstSetVisibilityAnchor,
        SecondSetVisibilityAnchor,
        ThirdSetVisibilityAnchor,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        fun getScanStartIndex(fingerprintResult: MethodFingerprintResult) =
            fingerprintResult.scanResult.patternScanResult!!.startIndex
        fun getScanEndIndex(fingerprintResult: MethodFingerprintResult) =
            fingerprintResult.scanResult.patternScanResult!!.endIndex

        // hide shorts comments button
        val shortsCommentsButtonResult = ShortsCommentsButtonFingerprint.result!!
        val shortsCommentsButtonMethod = shortsCommentsButtonResult.mutableMethod

        val checkCastAnchorIndex = getScanEndIndex(ShortsCommentsButtonParentFingerprint.also {
            it.resolve(context, shortsCommentsButtonMethod, shortsCommentsButtonResult.classDef)
        }.result!!)

        shortsCommentsButtonMethod.addInstructions(
            checkCastAnchorIndex + 1, """
                invoke-static {v${(shortsCommentsButtonMethod.instruction(checkCastAnchorIndex) as OneRegisterInstruction).registerA}}, L$INTEGRATIONS_PATCHES_PACKAGE/HideShortsCommentsButtonPatch;->hideShortsCommentsButton(Landroid/view/View;)V
            """
        )

        // hide fullscreen live chat button
        val liveChatFullscreenButtonMethod = LiveChatFullscreenButtonFingerprint.result!!.mutableMethod
        val constIndex = liveChatFullscreenButtonMethod.implementation?.instructions?.indexOfFirst {
            it.opcode.ordinal == Opcode.CONST.ordinal &&
            (it as WideLiteralInstruction).wideLiteral == CommentsResourcePatch.liveChatButtonId
        }!!

        liveChatFullscreenButtonMethod.addInstruction(
            constIndex + 1, """
                sput v${(liveChatFullscreenButtonMethod.instruction(constIndex) as OneRegisterInstruction).registerA}, $LIVE_CHAT_ACTIVITY_DESCRIPTOR->fullScreenLiveChatButtonId:I
            """
        )

        val setVisibilityAnchorFingerprints = listOf(
            FirstSetVisibilityAnchor,
            SecondSetVisibilityAnchor,
            ThirdSetVisibilityAnchor
        )
        setVisibilityAnchorFingerprints.resolve(
            context,
            listOf(LiveChatFullscreenButtonVisibilityFingerprint.result!!.classDef)
        )

        for (fingerprint in setVisibilityAnchorFingerprints) {
            with (fingerprint.result!!) {
                fun buildLiveChatButtonInvokeString(
                    method: MutableMethod,
                    index: Int,
                    instruction: BuilderInstruction35c = method.instruction(index) as BuilderInstruction35c,
                    firstRegister: Int = instruction.registerC,
                    secondRegister: Int = instruction.registerD,
                    classDescriptor: String = LIVE_CHAT_ACTIVITY_DESCRIPTOR,
                    methodName: String = LIVE_CHAT_RESOURCE_METHOD_NAME,
                    parameters: String = "Landroid/view/View;"
                ) = """
                    invoke-static {v$firstRegister, v$secondRegister}, $classDescriptor->$methodName($parameters)V
                    move-result v$secondRegister
                """

                val setVisibilityAnchorMethod = mutableMethod

                val invokeVirtualEndIndex = getScanEndIndex(this)
                setVisibilityAnchorMethod.addInstructions(
                    invokeVirtualEndIndex,
                    buildLiveChatButtonInvokeString(index = invokeVirtualEndIndex, method = mutableMethod)
                )

                if (fingerprint == FirstSetVisibilityAnchor) {
                    val invokeVirtualStartIndex = getScanStartIndex(this)
                    setVisibilityAnchorMethod.addInstructions(
                        invokeVirtualStartIndex,
                        buildLiveChatButtonInvokeString(index = invokeVirtualStartIndex, method = mutableMethod)
                    )
                }
            }
        }

        return PatchResultSuccess()
    }

    internal companion object {
        private const val INTEGRATIONS_PATCHES_PACKAGE = "app/revanced/integrations/patches"

        private const val LIVE_CHAT_ACTIVITY_DESCRIPTOR = "L$INTEGRATIONS_PATCHES_PACKAGE/patches/HideLiveChatFullScreenButtonPatch;"

        private const val LIVE_CHAT_RESOURCE_METHOD_NAME = "hideLiveChatFullScreenButton"
    }
}
