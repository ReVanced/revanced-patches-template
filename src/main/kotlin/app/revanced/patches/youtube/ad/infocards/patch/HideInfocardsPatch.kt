package app.revanced.patches.youtube.ad.infocards.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.ad.infocards.annotations.HideInfocardsCompatibility
import app.revanced.patches.youtube.ad.infocards.fingerprints.InfocardsIncognitoFingerprint
import app.revanced.patches.youtube.ad.infocards.fingerprints.InfocardsMethodCallFingerprint
import app.revanced.patches.youtube.ad.infocards.fingerprints.InfocardsIncognitoParentFingerprint
import app.revanced.patches.youtube.ad.infocards.resource.patch.HideInfocardsResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c

@Patch
@DependsOn([IntegrationsPatch::class, HideInfocardsResourcePatch::class])
@Name("hide-infocards")
@Description("Hides infocards in videos.")
@HideInfocardsCompatibility
@Version("0.0.1")
class HideInfocardsPatch : BytecodePatch(
    listOf(
        InfocardsIncognitoParentFingerprint,
        InfocardsMethodCallFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val parentResult = InfocardsIncognitoParentFingerprint.result
            ?: return PatchResultError("Parent fingerprint not resolved!")


        InfocardsIncognitoFingerprint.resolve(context, parentResult.classDef)
        val result = InfocardsIncognitoFingerprint.result
            ?: return PatchResultError("Required parent method could not be found.")

        val method = result.mutableMethod
        val implementation = method.implementation
            ?: return PatchResultError("Implementation not found.")

        val index = implementation.instructions.indexOfFirst { ((it as? BuilderInstruction35c)?.reference.toString() == "Landroid/view/View;->setVisibility(I)V") }

        method.replaceInstruction(index, """
            invoke-static {p1}, Lapp/revanced/integrations/patches/HideInfocardsPatch;->hideInfocardsIncognito(Landroid/view/View;)V
        """)

        val hideInfocardsCallResult = InfocardsMethodCallFingerprint.result!!
        val hideInfocardsCallMethod = hideInfocardsCallResult.mutableMethod

        val invokeInterfaceIndex = hideInfocardsCallResult.scanResult.patternScanResult!!.endIndex
        val toggleRegister = hideInfocardsCallMethod.implementation!!.registerCount - 1

        hideInfocardsCallMethod.addInstructions(
            invokeInterfaceIndex, """
                invoke-static {}, Lapp/revanced/integrations/patches/HideInfocardsPatch;->hideInfocardsMethodCall()Z
                move-result v$toggleRegister
                if-eqz v$toggleRegister, :hide_info_cards
            """, listOf(ExternalLabel("hide_info_cards", hideInfocardsCallMethod.instruction(invokeInterfaceIndex + 1)))
        )

        return PatchResultSuccess()
    }

}