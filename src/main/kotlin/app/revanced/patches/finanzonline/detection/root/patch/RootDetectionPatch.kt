package app.revanced.patches.finanzonline.detection.root.patch

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.finanzonline.detection.root.fingerprints.RootDetectionFingerprint

@Patch
@Name("remove-root-detection")
@Description("Removes the check for root permissions")
@Compatibility([Package("at.gv.bmf.bmf2go", arrayOf("2.2.0"))])
@Version("0.0.1")
class RootDetectionPatch : BytecodePatch(
    listOf(RootDetectionFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        RootDetectionFingerprint.result!!.mutableMethod.addInstructions(
            0,
            """
                sget-object v0, Ljava/lang/Boolean;->FALSE:Ljava/lang/Boolean;
                return-object v0
            """
        )
        return PatchResultSuccess()
    }
}
