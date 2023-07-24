package app.revanced.patches.octoapp.restrictions.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.octoapp.restrictions.fingereprints.GetFeatureEnabledFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("Enable Premium Features")
@Description("Removes Premium Feature lock from OctoApp")
@Compatibility([Package("de.crysxd.octoapp")])
@Version("0.0.1")
class RemovePremiumRestrictions : BytecodePatch(
    listOf(GetFeatureEnabledFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        GetFeatureEnabledFingerprint.result?.mutableMethod?.addInstructions(
            0,
            """
                 const/4 v1, 1
                 return v1
            """
        ) ?: return GetFeatureEnabledFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}
