package app.revanced.patches.music.misc.androidauto.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.music.misc.androidauto.annotations.BypassCertificateChecksCompatibility
import app.revanced.patches.music.misc.androidauto.fingerprints.BypassCertificateChecksFingerprint

@Patch
@Name("bypass-certificate-checks")
@Description("Bypasses certificate checks which prevent YouTube Music from working on Android Auto.")
@BypassCertificateChecksCompatibility
@Version("0.0.1")
class BypassCertificateChecksPatch : BytecodePatch(
    listOf(
        BypassCertificateChecksFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        var fixIndex = 0
        val result = BypassCertificateChecksFingerprint.result!!
        result.scanResult.stringsScanResult?.matches?.forEach{
            if (it.string.contains("No match")){
                fixIndex = it.index
            }
        }
        val method = BypassCertificateChecksFingerprint.result!!.mutableMethod

        method.replaceInstruction(fixIndex+2, "const/4 p1, 0x1")

        method.addInstruction(fixIndex+3,"return p1")

        return PatchResultSuccess()
    }
}
