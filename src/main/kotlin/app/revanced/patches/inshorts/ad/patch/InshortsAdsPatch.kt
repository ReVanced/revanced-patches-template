package app.revanced.patches.inshorts.ad

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.inshorts.ad.annotations.HideAdsCompatibility
import app.revanced.patches.inshorts.ad.fingerprints.InshortsAdsFingerprint

@Patch(
    name = "Hide ads",
    description = "Removes ads from Inshorts.",
    compatiblePackages = [
        CompatiblePackage(
            "com.nis.app"
        )
    ]
)
object HideAdsPatch : BytecodePatch(
    setOf(
        InshortsAdsFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        InshortsAdsFingerprint.result?.let { result ->
            result.apply {
                mutableMethod.addInstruction(
                    0,
                    """
                        return-void
                    """
                )
            }
        } ?: throw InshortsAdsFingerprint.exception
    }
}
