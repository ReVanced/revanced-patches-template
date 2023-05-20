package app.revanced.patches.trakt.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.trakt.annotations.UnlockProCompatibility
import app.revanced.patches.trakt.fingerprints.IsVIPEPFingerprint
import app.revanced.patches.trakt.fingerprints.IsVIPFingerprint

@Patch
@Name("unlock-pro")
@Description("Unlocks pro features.")
@UnlockProCompatibility
@Version("0.0.1")
class UnlockProPatch : BytecodePatch(
    listOf(IsVIPEPFingerprint, IsVIPFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        arrayOf(
            IsVIPFingerprint,
            IsVIPEPFingerprint
        ).map { it.result ?: return it.toErrorResult() }.forEach {
            it.mutableMethod.addInstructions(0, INSTRUCTIONS)
        }

        return PatchResultSuccess()
    }

    private companion object {
        const val INSTRUCTIONS = """
                   const/4 v0, 0x1
                   invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;
                   move-result-object v1
                   return-object v1
                """
    }
}