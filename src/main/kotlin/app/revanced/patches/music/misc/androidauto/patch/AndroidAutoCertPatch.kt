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
import app.revanced.patches.music.misc.androidauto.annotations.MusicAndroidAutoCompatibility
import app.revanced.patches.music.misc.androidauto.fingerprints.SHACertificateCheckFingerprint

@Patch
@Name("android-auto-cert-patch")
@Description("Enable youtube music in android auto in case of non-root version")
@MusicAndroidAutoCompatibility
@Version("0.0.1")
class AndroidAutoCertPatch : BytecodePatch(
    listOf(
        SHACertificateCheckFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        var fixIndex = 0
        val result = SHACertificateCheckFingerprint.result!!
        result.scanResult.stringsScanResult?.matches?.forEach{
            if (it.string.contains("No match")){
                fixIndex = it.index
            }
        }
        val method = SHACertificateCheckFingerprint.result!!.mutableMethod

        method.replaceInstruction(fixIndex+2, "const/4 p1, 0x1")

        method.addInstruction(fixIndex+3,"return p1")

        return PatchResultSuccess()
    }
}
