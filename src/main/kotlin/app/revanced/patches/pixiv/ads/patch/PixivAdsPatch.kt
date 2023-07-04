package app.revanced.patches.pixiv.ads.patch

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.pixiv.ads.fingerprints.PixivAdsFingerprint

@Patch
@Name("remove-ads")
@Description("Removes all ads from the app.")
@Compatibility([Package("jp.pxv.android")])
@Version("0.0.1")
class PixivAdsPatch : BytecodePatch(listOf(PixivAdsFingerprint)) {
    override fun execute(context: BytecodeContext): PatchResult {
        // Get the "isNotPremium" method. Note however that this is *not* the method that controls
        // whether the user is premium. This method is used to determine whether ads should be shown.
        // Normally, this function returns the following: !this.accountManager.isPremium
        val method = PixivAdsFingerprint.result!!.mutableClass.virtualMethods.first()

        // By always returning false, we can disable all ad placements.
        method.addInstructions(
            0, """
                const/4 v0, 0x0
                return v0
            """.trimIndent()
        )

        return PatchResultSuccess()
    }
}