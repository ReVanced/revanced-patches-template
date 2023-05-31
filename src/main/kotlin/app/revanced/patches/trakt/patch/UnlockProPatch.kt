package app.revanced.patches.trakt.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.trakt.annotations.UnlockProCompatibility
import app.revanced.patches.trakt.fingerprints.IsVIPEPFingerprint
import app.revanced.patches.trakt.fingerprints.IsVIPFingerprint
import app.revanced.patches.trakt.fingerprints.RealmUserSettingsFingerprint

@Patch
@Name("unlock-pro")
@Description("Unlocks pro features.")
@UnlockProCompatibility
@Version("0.0.1")
class UnlockProPatch : BytecodePatch(
    listOf(RealmUserSettingsFingerprint)
) {
    override suspend fun execute(context: BytecodeContext) {
        RealmUserSettingsFingerprint.result?.classDef?.let { realUserSettingsClass ->
            arrayOf(IsVIPFingerprint, IsVIPEPFingerprint).onEach { fingerprint ->
                // Resolve both fingerprints on the same class.
                if (!fingerprint.resolve(context, realUserSettingsClass))
                    fingerprint.error()
            }.forEach { fingerprint ->
                // Return true for both VIP check methods.
                fingerprint.result?.mutableMethod?.addInstructions(0, RETURN_TRUE_INSTRUCTIONS)
                    ?: fingerprint.error()
            }
        } ?: RealmUserSettingsFingerprint.error()
    }

    private companion object {
        const val RETURN_TRUE_INSTRUCTIONS =
            """
                const/4 v0, 0x1
                invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;
                move-result-object v1
                return-object v1
            """
    }
}