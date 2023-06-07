package app.revanced.patches.youtube.layout.hide.infocards.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.hide.infocards.annotations.HideInfocardsCompatibility
import app.revanced.patches.youtube.layout.hide.infocards.fingerprints.InfocardsIncognitoFingerprint
import app.revanced.patches.youtube.layout.hide.infocards.fingerprints.InfocardsIncognitoParentFingerprint
import app.revanced.patches.youtube.layout.hide.infocards.fingerprints.InfocardsMethodCallFingerprint
import app.revanced.patches.youtube.layout.hide.infocards.resource.patch.HideInfocardsResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.FiveRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction

@Patch
@DependsOn([IntegrationsPatch::class, HideInfocardsResourcePatch::class])
@Name("hide-info-cards")
@Description("Hides info cards in videos.")
@HideInfocardsCompatibility
@Version("0.0.1")
class HideInfoCardsPatch : BytecodePatch(
    listOf(
        InfocardsIncognitoParentFingerprint,
        InfocardsMethodCallFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
       InfocardsIncognitoFingerprint.also {
            it.resolve(context, InfocardsIncognitoParentFingerprint.result!!.classDef)
        }.result!!.mutableMethod.apply {
            val invokeInstructionIndex = implementation!!.instructions.indexOfFirst {
                it.opcode.ordinal == Opcode.INVOKE_VIRTUAL.ordinal &&
                        ((it as ReferenceInstruction).reference.toString() == "Landroid/view/View;->setVisibility(I)V")
            }

           addInstruction(
               invokeInstructionIndex,
               "invoke-static {v${getInstruction<FiveRegisterInstruction>(invokeInstructionIndex).registerC}}," +
                       " Lapp/revanced/integrations/patches/HideInfoCardsPatch;->hideInfoCardsIncognito(Landroid/view/View;)V"
           )
        }

        with(InfocardsMethodCallFingerprint.result!!) {
            val hideInfoCardsCallMethod = mutableMethod

            val invokeInterfaceIndex = scanResult.patternScanResult!!.endIndex
            val toggleRegister = hideInfoCardsCallMethod.implementation!!.registerCount - 1

            hideInfoCardsCallMethod.addInstructionsWithLabels(
                invokeInterfaceIndex,
                """
                    invoke-static {}, Lapp/revanced/integrations/patches/HideInfoCardsPatch;->hideInfoCardsMethodCall()Z
                    move-result v$toggleRegister
                    if-nez v$toggleRegister, :hide_info_cards
                """,
                ExternalLabel(
                    "hide_info_cards", hideInfoCardsCallMethod.getInstruction(invokeInterfaceIndex + 1)
                )
            )
        }

        return PatchResultSuccess()
    }
}