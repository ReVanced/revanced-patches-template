package app.revanced.patches.grindr.firebase.patch

import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.warnwetter.misc.firebasegetcert.patch.FirebaseGetCertPatch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patches.grindr.firebase.fingerprints.GetMessagingCertFingerprint
import app.revanced.patches.grindr.firebase.fingerprints.GetRegistrationCertFingerprint
import app.revanced.patches.grindr.Constants.SPOOFED_PACKAGE_SIGNATURE

class FirebaseGetCertPatchGrindr : BytecodePatch(
    listOf(
        GetRegistrationCertFingerprint,
        GetMessagingCertFingerprint
    )
) {

    val delegate = FirebaseGetCertPatch()

    override fun execute(context: BytecodeContext) {
        val registrationCertMethod = GetRegistrationCertFingerprint.result!!.mutableMethod
        val messagingCertMethod = GetMessagingCertFingerprint.result!!.mutableMethod

        val mutableMethods = arrayOf(registrationCertMethod, messagingCertMethod)

        delegate.patchPackageSignature(SPOOFED_PACKAGE_SIGNATURE, mutableMethods)
    }
}