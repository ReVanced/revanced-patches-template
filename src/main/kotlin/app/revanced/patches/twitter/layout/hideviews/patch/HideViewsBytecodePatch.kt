package app.revanced.patches.twitter.layout.hideviews.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.extensions.removeInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.twitter.layout.hideviews.fingerprints.InlineActionTypesFingerprint
import app.revanced.patches.twitter.layout.hideviews.fingerprints.TweetStatsContainerConstructorFingerprint
import app.revanced.patches.twitter.layout.hideviews.fingerprints.TweetStatsContainerWrapperConstructorFingerprint
import app.revanced.patches.twitter.layout.hideviews.fingerprints.TweetStatsViewDelegateBinderFingerprint
import org.jf.dexlib2.Opcode

class HideViewsBytecodePatch : BytecodePatch(
    listOf(
        InlineActionTypesFingerprint,
        TweetStatsContainerWrapperConstructorFingerprint,
        TweetStatsContainerConstructorFingerprint,
        TweetStatsViewDelegateBinderFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        removeViewsFromTimeline(context)
        removeTweetStatViewInitializer(context)
        removeTweetStatViewWrapperInitializer(context)
        removeViewDelegateBinderSubscription(context)
        return PatchResultSuccess()
    }

    private fun removeViewsFromTimeline(context: BytecodeContext): PatchResult {
        val result = InlineActionTypesFingerprint.result!!
        val method = result.mutableMethod
        val addViewsToActionBarMethodFingerprint = object : MethodFingerprint(
            opcodes = listOf(
                Opcode.INVOKE_STATIC,
                Opcode.MOVE_RESULT,
                Opcode.IF_EQZ,
                Opcode.SGET_OBJECT,
                Opcode.INVOKE_VIRTUAL,
                Opcode.IF_EQZ,
            )
        ) {}
        val addViewsToActionBarMethodLine = addViewsToActionBarMethodFingerprint.also {
            it.resolve(context, method, result.classDef)
        }.result!!.scanResult.patternScanResult!!.endIndex - 1
        method.removeInstruction(addViewsToActionBarMethodLine)
        return PatchResultSuccess()
    }

    private fun removeTweetStatViewInitializer(context: BytecodeContext) {
        val result = TweetStatsContainerConstructorFingerprint.result!!
        val method = result.mutableMethod
        val returnFingerprint = object : MethodFingerprint(
            opcodes = listOf(Opcode.RETURN_VOID)
        ) {}
        val addViewsToActionBarMethodLine = returnFingerprint.also {
            it.resolve(context, method, result.classDef)
        }.result!!.scanResult.patternScanResult!!.endIndex - 3
        method.removeInstructions(addViewsToActionBarMethodLine, 2)
    }

    private fun removeTweetStatViewWrapperInitializer(context: BytecodeContext) {
        val wrapperResult = TweetStatsContainerWrapperConstructorFingerprint.result!!
        val wrapperMethod = wrapperResult.mutableMethod
        val wrapperReturnFingerprint = object : MethodFingerprint(
            opcodes = listOf(
                Opcode.IGET_OBJECT,
                Opcode.INVOKE_STATIC,
                Opcode.MOVE_RESULT_OBJECT,
                Opcode.IPUT_OBJECT,
                Opcode.RETURN_VOID,
            )
        ) {}
        val setupVariableLine = wrapperReturnFingerprint.also {
            it.resolve(context, wrapperMethod, wrapperResult.classDef)
        }.result!!.scanResult.patternScanResult!!.startIndex - 4
        wrapperMethod.removeInstructions(setupVariableLine, 3)
    }

    private fun removeViewDelegateBinderSubscription(context: BytecodeContext) {
        val binderResult = TweetStatsViewDelegateBinderFingerprint.result!!
        val binderMethod = binderResult.mutableMethod
        val bindLine = binderResult.scanResult.patternScanResult!!.startIndex - 4
        binderMethod.removeInstructions(bindLine, 9)
    }
}
