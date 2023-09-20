package app.revanced.patches.youtube.layout.hide.endscreencards

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.layout.hide.endscreencards.fingerprints.LayoutCircleFingerprint
import app.revanced.patches.youtube.layout.hide.endscreencards.fingerprints.LayoutIconFingerprint
import app.revanced.patches.youtube.layout.hide.endscreencards.fingerprints.LayoutVideoFingerprint
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21c

@Patch(
    name = "Hide endscreen cards",
    description = "Hides the suggested video cards at the end of a video in fullscreen.",
    dependencies = [
        IntegrationsPatch::class,
        HideEndscreenCardsResourcePatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.16.37",
                "18.19.35",
                "18.20.39",
                "18.23.35",
                "18.29.38",
                "18.32.39"
            ]
        )
    ]
)
@Suppress("unused")
object HideEndscreenCardsPatch : BytecodePatch(
    setOf(
        LayoutCircleFingerprint,
        LayoutIconFingerprint,
        LayoutVideoFingerprint,
    )
) {
    override fun execute(context: BytecodeContext) {
        fun MethodFingerprint.injectHideCall() {
            val layoutResult = result ?: throw exception
            layoutResult.mutableMethod.apply {
                val insertIndex = layoutResult.scanResult.patternScanResult!!.endIndex + 1
                val viewRegister = getInstruction<Instruction21c>(insertIndex - 1).registerA

                addInstruction(
                    insertIndex,
                    "invoke-static { v$viewRegister }, Lapp/revanced/integrations/patches/HideEndscreenCardsPatch;->hideEndscreen(Landroid/view/View;)V"
                )
            }
        }

        listOf(
            LayoutCircleFingerprint,
            LayoutIconFingerprint,
            LayoutVideoFingerprint
        ).forEach(MethodFingerprint::injectHideCall)
    }
}
