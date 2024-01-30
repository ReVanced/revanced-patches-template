package app.revanced.patches.grindr.unlimited.patch

import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult

import app.revanced.patches.grindr.unlimited.fingerprints.*
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.MethodFingerprint
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod

import app.revanced.patches.grindr.firebase.patch.FirebaseGetCertPatchGrindr



@Patch(
    name = "Unlock unlimited",
    description = "Unlocks unlimited features.",
    dependencies = [FirebaseGetCertPatchGrindr::class],
    compatiblePackages = [
        CompatiblePackage("com.grindrapp.android", ["9.18.4"]),
    ],
)
class UnlockUnlimitedPatch : BytecodePatch(
    setOf(
        HasFeatureFingerprint,
        IsFreeFingerprint,
        IsNoPlusUpsellFingerprint,
        IsNoXtraUpsellFingerprint,
        InnaccessibleProfileManagerbFingerprint,
        InnaccessibleProfileManagerdFingerprint
    )
) {

    interface BytecodePatchable {
        fun execute(context: BytecodeContext)
    }

    class Patch(val target: MethodFingerprint, val bytecode: String, val offset: Int = 0) : BytecodePatchable {
        override fun execute(context: BytecodeContext) {
            target.result?.let { result ->
                result.mutableMethod.apply {
                    replaceInstructions(offset, bytecode)
                }
            }
        }
    }

    class PatchMutableMethod(val target: MutableMethod, val bytecode: String, val offset: Int = 0 ) : BytecodePatchable {
        override fun execute(context: BytecodeContext) {
            target.apply {
                replaceInstructions(offset, bytecode)
            }
        }
    }

    override fun execute(context: BytecodeContext) {

        val trueBytecode = """
            const/4 v0, 0x1
            return v0
        """

        val falseBytecode = """
            const/4 v0, 0x0
            return v0
        """

        val IsPlus = context.toMethodWalker(IsNoPlusUpsellFingerprint.result!!.method).nextMethod(0, true).getMethod() as MutableMethod
        val IsXtra = context.toMethodWalker(IsNoPlusUpsellFingerprint.result!!.method).nextMethod(3, true).getMethod() as MutableMethod
        val IsUnlimited = context.toMethodWalker(IsNoPlusUpsellFingerprint.result!!.method).nextMethod(6, true).getMethod() as MutableMethod

        /*
            Based on: https://github.com/ElJaviLuki/GrindrPlus
        */

        val patches = arrayOf(
            Patch(HasFeatureFingerprint, trueBytecode),
            Patch(IsFreeFingerprint, falseBytecode),
            Patch(IsNoPlusUpsellFingerprint,trueBytecode),
            Patch(IsNoXtraUpsellFingerprint, trueBytecode),
            PatchMutableMethod(IsPlus, falseBytecode),
            PatchMutableMethod(IsUnlimited, trueBytecode),
            PatchMutableMethod(IsXtra, falseBytecode),
            Patch(InnaccessibleProfileManagerbFingerprint, trueBytecode),
            Patch(InnaccessibleProfileManagerdFingerprint, falseBytecode)
        )

        for (patch in patches) {
            patch.execute(context)
        }
    }
    
}