package app.revanced.patches.tiktok.misc.loginfallback.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.tiktok.misc.loginfallback.annotations.TikTokWebLoginCompatibility

@Patch
@Name("tiktok-web-login")
@Description("Allows logging in with a Google account.")
@TikTokWebLoginCompatibility
@Version("0.0.1")
class TikTokLoginFallbackPatch : BytecodePatch() {
    override fun execute(data: BytecodeData): PatchResult {
        val googleOneTapAuth = data.findClass("Lcom/bytedance/lobby/google/GoogleOneTapAuth;")!!.resolve()
        val googleOneTapAuthCheck = googleOneTapAuth.methods.first { it.returnType == "Z" && it.parameters.size == 0 }
        googleOneTapAuthCheck.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return v0
            """
        )
        val googleAuth = data.findClass("Lcom/bytedance/lobby/google/GoogleAuth;")!!.resolve()
        val googleAuthCheck = googleAuth.methods.first { it.returnType == "Z" && it.parameters.size == 0 }
        googleAuthCheck.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return v0
            """
        )
        return PatchResultSuccess()
    }
}