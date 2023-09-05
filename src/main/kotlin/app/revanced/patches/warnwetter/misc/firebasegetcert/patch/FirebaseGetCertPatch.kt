package app.revanced.patches.warnwetter.misc.firebasegetcert.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.warnwetter.misc.firebasegetcert.annotations.FirebaseGetCertPatchCompatibility
import app.revanced.patches.warnwetter.misc.firebasegetcert.fingerprints.GetMessagingCertFingerprint
import app.revanced.patches.warnwetter.misc.firebasegetcert.fingerprints.GetReqistrationCertFingerprint
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod

@Name("Spoof cert patch")
@Description("Spoofs the X-Android-Cert header.")
@FirebaseGetCertPatchCompatibility
class FirebaseGetCertPatch : BytecodePatch(
    listOf(
        GetReqistrationCertFingerprint,
        GetMessagingCertFingerprint
    )
) {

    fun patchPackageSignature(packageSignature: String, mutableMethods: Array<MutableMethod>) {
        val spoofedInstruction =
            """
                const-string v0, "$packageSignature"
                return-object v0
            """

        for (mutableMethod in mutableMethods) {
            mutableMethod.addInstructions(
                0,
                spoofedInstruction
            )
        }
    }

    override fun execute(context: BytecodeContext) {
        val registrationCertMethod = GetReqistrationCertFingerprint.result!!.mutableMethod
        val messagingCertMethod = GetMessagingCertFingerprint.result!!.mutableMethod

        val mutableMethods = arrayOf(registrationCertMethod, messagingCertMethod)

        this.patchPackageSignature("0799DDF0414D3B3475E88743C91C0676793ED450", mutableMethods)
    }
}