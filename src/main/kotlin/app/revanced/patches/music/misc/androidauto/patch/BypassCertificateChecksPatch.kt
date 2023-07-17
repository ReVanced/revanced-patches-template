package app.revanced.patches.music.misc.androidauto.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.music.annotations.MusicCompatibility
import app.revanced.patches.music.misc.androidauto.fingerprints.CheckCertificateFingerprint

@Patch
@Name("Bypass certificate checks")
@Description("Bypasses certificate checks which prevent YouTube Music from working on Android Auto.")
@MusicCompatibility
@Version("0.0.1")
class BypassCertificateChecksPatch : BytecodePatch(
    listOf(CheckCertificateFingerprint)
) {
    override suspend fun execute(context: BytecodeContext) {
        CheckCertificateFingerprint.result?.apply {
            mutableMethod.addInstructions(
                0, """
                const/4 v0, 0x1
                return v0
                """
            )
        } ?: CheckCertificateFingerprint.error()
    }
}
