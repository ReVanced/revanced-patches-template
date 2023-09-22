package app.revanced.patches.cieid.restrictions.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.cieid.restrictions.fingerprints.RootCheckerFingerprint

@Patch
@Name("root-checker-bypass")
@Description("Removes the restriction of using the app with root or custom rom")
@Compatibility([Package("it.ipzs.cieid")])
class BypassRootCheckerPatch : BytecodePatch(listOf(RootCheckerFingerprint)) {
    override fun execute(context: BytecodeContext) {
        val result = RootCheckerFingerprint.result ?: throw RootCheckerFingerprint.exception
        result.apply {
            mutableMethod.addInstruction(1, "return-void")
        }
    }

}