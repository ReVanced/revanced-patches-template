package app.revanced.patches.youtube.layout.autorepeat.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.layout.autorepeat.annotations.AutoRepeatCompatibility
import app.revanced.patches.youtube.layout.autorepeat.fingerprints.AutoRepeatParentFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch

@Patch(include = false)
@Dependencies(dependencies = [IntegrationsPatch::class])
@Name("autorepeat-by-default")
@Description("Enables auto repeating of videos by default.")
@AutoRepeatCompatibility
@Version("0.0.1")
class AutoRepeatPatch : BytecodePatch(
    listOf(
        AutoRepeatParentFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val parentResult = AutoRepeatParentFingerprint.result
            ?: return PatchResultError("ParentFingerprint did not resolve.")

        //this one needs to be called when Setting returns true
        val playMethod = parentResult.method;

        return PatchResultError("Not yet implemented")
    }
}
