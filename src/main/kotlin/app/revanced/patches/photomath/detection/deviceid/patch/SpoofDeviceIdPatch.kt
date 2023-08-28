package app.revanced.patches.photomath.detection.deviceid.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.photomath.detection.deviceid.fingerprints.GetDeviceIdFingerprint
import app.revanced.patches.photomath.detection.signature.patch.SignatureDetectionPatch
import kotlin.random.Random

@Patch
@DependsOn([SignatureDetectionPatch::class])
@Name("Spoof device ID")
@Description("Spoofs device ID - may prevent manual bans by developers.")
@Compatibility([Package("com.microblink.photomath")])
class SpoofDeviceIdPatch : BytecodePatch(
    listOf(GetDeviceIdFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        GetDeviceIdFingerprint.result?.apply {
            val fakeId = Random.nextLong().toString(16)
            mutableMethod.replaceInstructions(
                0, """
                const-string v0, "$fakeId"
                return-object v0
            """.trimIndent()
            )
        } ?: throw GetDeviceIdFingerprint.exception
    }
}