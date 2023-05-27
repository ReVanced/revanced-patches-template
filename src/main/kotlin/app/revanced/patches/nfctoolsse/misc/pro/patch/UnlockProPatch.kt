package app.revanced.patches.nfctoolsse.misc.pro.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.nfctoolsse.misc.pro.annotations.UnlockProCompatibility
import app.revanced.patches.nfctoolsse.misc.pro.fingerprints.IsLicenseRegisteredFingerprint


@Patch
@Name("unlock-pro")
@Description("Unlocks all pro features.")
@Version("0.0.1")
@UnlockProCompatibility
class UnlockProPatch : BytecodePatch(
    listOf(
        IsLicenseRegisteredFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        IsLicenseRegisteredFingerprint.result?.mutableMethod?.apply {
            addInstructions(
                0,
                """
                    const/4 v0, 0x1
                    return v0
                """
            )
        } ?: IsLicenseRegisteredFingerprint.error()
    }

}