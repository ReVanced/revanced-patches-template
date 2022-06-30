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
import app.revanced.patches.youtube.layout.autorepeat.fingerprints.AutoRepeatFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch

@Patch
@Dependencies(dependencies = [IntegrationsPatch::class])
@Name("disable-auto-repeat")
@Description("Disable auto repeat")
@AutoRepeatCompatibility
@Version("0.0.1")
class AutoRepeatPatch : BytecodePatch(
    listOf(
        AutoRepeatFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        return PatchResultError("Not yet implemented")
    }
}
