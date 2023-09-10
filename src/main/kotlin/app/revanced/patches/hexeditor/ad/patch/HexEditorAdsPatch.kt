package app.revanced.patches.hexeditor.ad

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.hexeditor.ad.fingerprints.PrimaryAdsFingerprint

@Patch(
    name = "Disable ads",
    description = "Disables ads in HexEditor.",
    compatiblePackages = [
        CompatiblePackage(
            "com.myprog.hexedit"
        )
    ]
)
object HexEditorAdsPatch : BytecodePatch(
    setOf(
        PrimaryAdsFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        val method = PrimaryAdsFingerprint.result!!.mutableMethod
        
        method.replaceInstructions(
            0,
            """
                const/4 v0, 0x1
                return v0
            """
        )
    }
}
