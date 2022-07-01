package app.revanced.patches.youtube.misc.hdrbrightness.patch;

import app.revanced.patcher.annotation.Description;
import app.revanced.patcher.annotation.Name;
import app.revanced.patcher.annotation.Version;
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch;
import app.revanced.patcher.patch.impl.BytecodePatch;
import app.revanced.patcher.util.smali.toBuilderInstruction
import app.revanced.patches.youtube.misc.hdrbrightness.annotations.HDRBrightnessCompatibility;
import app.revanced.patches.youtube.misc.hdrbrightness.fingerprints.HDRBrightnessFingerprint
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction

@Patch
@Name("hdr-max-brightness")
@Description("Set brightness to max for HDR videos.")
@HDRBrightnessCompatibility
@Version("0.0.1")
class HDRBgithnessPatch : BytecodePatch(
    listOf(
        HDRBrightnessFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val result = HDRBrightnessFingerprint.result
            ?: return PatchResultError("HDRBrightnessFingerprint could not resolve the method!")


        val method = result.mutableMethod

        /*
        val debugString = method.definingClass + " :: " + method.name + " :: " + method.implementation!!.instructions.size + " :: " + (method.implementation!!.instructions.get(10) as NarrowLiteralInstruction).narrowLiteral + " :: " + (-1.0f).toRawBits();
        if(true)
            return PatchResultError(debugString)
         */

        method.addInstructions(
            11, """
           invoke-static {v2}, Lapp/revanced/integrations/patches/HDRMaxBrightnessPatch;->getHDRBrightness(F)F
           move-result v2 
        """
        )

        return PatchResultSuccess()
    }
}
