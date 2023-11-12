package app.revanced.patches.facebook.ads.story

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.facebook.ads.story.fingerprints.FetchMoreAdsFingerprint
import app.revanced.patches.facebook.ads.story.fingerprints.AdsInsertionFingerprint

@Patch(
    name = "Disable story ads",
    description = "Disables the ads in the Facebook app stories.",
    compatiblePackages = [CompatiblePackage("com.facebook.katana")]
)
@Suppress("unused")
object DisableStoryAdsPatch : BytecodePatch(
    setOf(FetchMoreAdsFingerprint, AdsInsertionFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        allFingerprints.forEach { fingerprint ->
            fingerprint.result?.mutableMethod?.replaceInstruction(0, "return-void")
                ?: throw fingerprint.exception
        }
    }

    private val allFingerprints = setOf(FetchMoreAdsFingerprint, AdsInsertionFingerprint)
}
