package app.revanced.patches.tiktok.misc.spoofsignature.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.tiktok.misc.spoofsignature.annotations.SpoofSignatureCompatibility
import app.revanced.patches.tiktok.misc.spoofsignature.fingerprints.AwemeHostApplicationAttachFingerprint

@Patch
@Name("Signature Spoof")
@Description("Fix TikTok issues relate to signature.")
@SpoofSignatureCompatibility
@Version("0.0.1")
class SpoofSignaturePatch : BytecodePatch(
    listOf(
        AwemeHostApplicationAttachFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val method = AwemeHostApplicationAttachFingerprint.result!!.mutableMethod
        method.addInstruction(
            0,
            "invoke-static {p1}, Lapp/revanced/tiktok/signature/SignatureSpoof;->enable(Landroid/content/Context;)V"
        )
        return PatchResultSuccess()
    }
}
