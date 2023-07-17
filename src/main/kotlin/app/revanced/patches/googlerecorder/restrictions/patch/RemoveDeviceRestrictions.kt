package app.revanced.patches.googlerecorder.restrictions.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.googlerecorder.restrictions.fingereprints.OnApplicationCreateFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("Remove device restrictions")
@Description("Removes restrictions from using the app on any device.")
@Compatibility([Package("com.google.android.apps.recorder")])
@Version("0.0.1")
class RemoveDeviceRestrictions : BytecodePatch(
    listOf(OnApplicationCreateFingerprint)
) {
    override suspend fun execute(context: BytecodeContext) {
        OnApplicationCreateFingerprint.result?.let {
            val featureStringIndex = it.scanResult.stringsScanResult!!.matches.first().index

            it.mutableMethod.apply {
                // Remove check for device restrictions.
                removeInstructions(featureStringIndex - 2, 5)

                val featureAvailableRegister = getInstruction<OneRegisterInstruction>(featureStringIndex).registerA

                // Override "isPixelDevice()" to return true.
                addInstruction(featureStringIndex, "const/4 v$featureAvailableRegister, 0x1")
            }
        } ?: OnApplicationCreateFingerprint.error()
    }
}
