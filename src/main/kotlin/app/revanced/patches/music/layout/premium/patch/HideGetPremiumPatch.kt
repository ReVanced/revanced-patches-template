package app.revanced.patches.music.layout.premium.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.music.layout.premium.annotations.HideGetPremiumCompatibility
import app.revanced.patches.music.layout.premium.fingerprints.HideGetPremiumFingerprint
import app.revanced.patches.music.layout.premium.fingerprints.HideGetPremiumParentFingerprint
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c

@Patch
@Name("hide-get-premium")
@Description("Removes all \"Get Premium\" evidences from the avatar menu.")
@HideGetPremiumCompatibility
@Version("0.0.1")
class HideGetPremiumPatch : BytecodePatch(
    listOf(
        HideGetPremiumParentFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val parentResult = HideGetPremiumParentFingerprint.result!!
        HideGetPremiumFingerprint.resolve(data, parentResult.classDef)

        val method = HideGetPremiumFingerprint.result!!.mutableMethod

        val firstIndex = method.implementation!!.instructions.indexOfFirst {
            ((it as? BuilderInstruction35c)?.reference.toString() == "Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z")
        }
        method.replaceInstruction(
            firstIndex - 6, """
            const/4 v1, 0x0
        """
        )

        val secondIndex = method.implementation!!.instructions.indexOfFirst {
            ((it as? BuilderInstruction35c)?.reference.toString() == "Landroid/view/View;->setVisibility(I)V")
        }
        method.addInstructions(
            secondIndex - 4, """
            const/16 v0, 0x8
        """
        )

        return PatchResultSuccess()
    }
}
