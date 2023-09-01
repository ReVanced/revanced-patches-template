package app.revanced.patches.grindr.patch.bytecode

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.grindr.fingerprints.GetMessagingCertFingerprint
import app.revanced.patches.grindr.fingerprints.GetReqistrationCertFingerprint
import app.revanced.patches.grindr.Constants.SPOOFED_PACKAGE_SIGNATURE

import app.revanced.patches.grindr.annotations.GrindrPatchCompatibility

@Patch
@Name("Firebase patch")
@Description("Allows Grindr to run after being modified by ReVanced.")
@GrindrPatchCompatibility

class FirebaseGetCertPatch : BytecodePatch(
    listOf(
        GetReqistrationCertFingerprint,
        GetMessagingCertFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val spoofedInstruction =
            """
                const-string v0, "$SPOOFED_PACKAGE_SIGNATURE"
                return-object v0
            """

        val registrationCertMethod = GetReqistrationCertFingerprint.result!!.mutableMethod
        val messagingCertMethod = GetMessagingCertFingerprint.result!!.mutableMethod

        registrationCertMethod.addInstructions(
            0,
            spoofedInstruction
        )
        messagingCertMethod.addInstructions(
            0,
            spoofedInstruction
        )

        return PatchResultSuccess()
    }
}