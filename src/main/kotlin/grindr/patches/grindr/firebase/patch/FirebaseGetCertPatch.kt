package app.revanced.patches.grindr.firebase.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patches.grindr.firebase.fingerprints.GetMessagingCertFingerprint
import app.revanced.patches.grindr.firebase.fingerprints.GetRegistrationCertFingerprint
import app.revanced.patches.grindr.Constants.SPOOFED_PACKAGE_SIGNATURE

class FirebaseGetCertPatchGrindr : BytecodePatch(
    setOf(
        GetRegistrationCertFingerprint,
        GetMessagingCertFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        val spoofedInstruction =
        """
            const-string v0, "$SPOOFED_PACKAGE_SIGNATURE"
            return-object v0
        """

        val registrationCertMethod = GetRegistrationCertFingerprint.result!!.mutableMethod
        val messagingCertMethod = GetMessagingCertFingerprint.result!!.mutableMethod

        registrationCertMethod.addInstructions(
            0,
            spoofedInstruction
        )
        messagingCertMethod.addInstructions(
            0,
            spoofedInstruction
        )
    }
}