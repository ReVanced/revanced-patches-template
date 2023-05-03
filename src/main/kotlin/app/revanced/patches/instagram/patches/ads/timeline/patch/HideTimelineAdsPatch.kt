package app.revanced.patches.instagram.patches.ads.timeline.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.*
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.instagram.patches.ads.timeline.fingerprints.MediaFingerprint
import app.revanced.patches.instagram.patches.ads.timeline.fingerprints.ShowAdFingerprint
import app.revanced.patches.instagram.patches.ads.timeline.fingerprints.ads.GenericMediaAdFingerprint
import app.revanced.patches.instagram.patches.ads.timeline.fingerprints.ads.MediaAdFingerprint
import app.revanced.patches.instagram.patches.ads.timeline.fingerprints.ads.PaidPartnershipAdFingerprint
import app.revanced.patches.instagram.patches.ads.timeline.fingerprints.ads.ShoppingAdFingerprint
import org.jf.dexlib2.iface.instruction.FiveRegisterInstruction
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("hide-timeline-ads")
@Description("Removes ads from the timeline.")
@Compatibility([Package("com.instagram.android")])
@Version("0.0.1")
class HideTimelineAdsPatch : BytecodePatch(
    listOf(
        ShowAdFingerprint,
        MediaFingerprint,
        PaidPartnershipAdFingerprint // Unlike the other ads this one is resolved from all classes.
    )
) {
    override fun execute(context: BytecodeContext) {
        // region Resolve required methods to check for ads.

        ShowAdFingerprint.result ?: ShowAdFingerprint.error()

        PaidPartnershipAdFingerprint.result ?: PaidPartnershipAdFingerprint.error()

        MediaFingerprint.result?.let {
            GenericMediaAdFingerprint.resolve(context, it.classDef)
            ShoppingAdFingerprint.resolve(context, it.classDef)

            return@let
        } ?: MediaFingerprint.error()

        // endregion

        ShowAdFingerprint.result!!.apply {
            // region Create instructions.

            val scanStart = scanResult.patternScanResult!!.startIndex
            val jumpIndex = scanStart - 1

            val mediaInstanceRegister = (mutableMethod.instruction(scanStart) as FiveRegisterInstruction).registerC
            val freeRegister = (mutableMethod.instruction(jumpIndex) as OneRegisterInstruction).registerA

            val returnFalseLabel = "an_ad"

            val checkForAdInstructions =
                listOf(GenericMediaAdFingerprint, PaidPartnershipAdFingerprint, ShoppingAdFingerprint)
                    .map(MediaAdFingerprint::toString)
                    .joinToString("\n") {
                        """ 
                            invoke-virtual {v$mediaInstanceRegister}, $it
                            move-result v$freeRegister
                            if-nez v$freeRegister, :$returnFalseLabel
                        """.trimIndent()
                    }.let { "$it\nconst/4 v0, 0x1\nreturn v0" }

            // endregion

            // region Patch.

            val insertIndex = scanStart + 3

            mutableMethod.addInstructions(
                insertIndex,
                checkForAdInstructions,
                listOf(
                    ExternalLabel(
                        returnFalseLabel,
                        mutableMethod.instruction(mutableMethod.implementation!!.instructions.size - 2 /* return false = ad */)
                    )
                )
            )

            // endregion

            // region Jump to checks for ads from previous patch.

            mutableMethod.apply {
                addInstructions(
                    jumpIndex + 1,
                    "if-nez v$freeRegister, :start_check",
                    listOf(ExternalLabel("start_check", instruction(insertIndex)))
                )
            }.removeInstruction(jumpIndex)

            // endregion
        }

    }
}