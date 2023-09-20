package app.revanced.patches.youtube.misc.fix.backtoexitgesture

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.OnBackPressedFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.RecyclerViewScrollingFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.RecyclerViewTopScrollingFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.RecyclerViewTopScrollingParentFingerprint

@Patch(description = "Fixes the swipe back to exit gesture.")
@Suppress("unused")
object FixBackToExitGesturePatch : BytecodePatch(
    setOf(
        RecyclerViewTopScrollingParentFingerprint,
        RecyclerViewScrollingFingerprint,
        OnBackPressedFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        RecyclerViewTopScrollingFingerprint.apply {
            resolve(
                context,
                RecyclerViewTopScrollingParentFingerprint.result?.classDef
                    ?: throw RecyclerViewTopScrollingParentFingerprint.exception
            )
        }

        mapOf(
            RecyclerViewTopScrollingFingerprint to IntegrationsMethod(
                methodName = "onTopView"
            ),
            RecyclerViewScrollingFingerprint to IntegrationsMethod(
                methodName = "onScrollingViews"
            ),
            OnBackPressedFingerprint to IntegrationsMethod(
                "p0", "onBackPressed", "Lcom/google/android/apps/youtube/app/watchwhile/WatchWhileActivity;"
            )
        ).forEach { (fingerprint, target) -> fingerprint.injectCall(target) }
    }

    /**
     * Inject a call to a method from the integrations.
     *
     * @param targetMethod The target method to call.
     */
    fun MethodFingerprint.injectCall(targetMethod: IntegrationsMethod) = result?.apply {
        mutableMethod.addInstruction(
            scanResult.patternScanResult!!.endIndex, targetMethod.toString()
        )
    } ?: throw this.exception

    /**
     * A reference to a method from the integrations for [FixBackToExitGesturePatch].
     *
     * @param register The method registers.
     * @param methodName The method name.
     * @param parameterTypes The parameters of the method.
     */
    data class IntegrationsMethod(
        val register: String = "", val methodName: String, val parameterTypes: String = ""
    ) {
        override fun toString() =
            "invoke-static {$register}, Lapp/revanced/integrations/patches/FixBackToExitGesturePatch;->$methodName($parameterTypes)V"
    }
}