package app.revanced.patches.tiktok.misc.settings.patch

import app.revanced.patcher.BytecodeContext
import app.revanced.extensions.error
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.tiktok.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.tiktok.misc.settings.annotations.SettingsCompatibility
import app.revanced.patches.tiktok.misc.settings.fingerprints.AboutPageFingerprint
import app.revanced.patches.tiktok.misc.settings.fingerprints.AdPersonalizationActivityOnCreateFingerprint
import app.revanced.patches.tiktok.misc.settings.fingerprints.SettingsOnViewCreatedFingerprint
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.FiveRegisterInstruction
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Patch
@DependsOn([IntegrationsPatch::class])
@Name("Settings")
@Description("Adds ReVanced settings to TikTok.")
@SettingsCompatibility
@Version("0.0.1")
class SettingsPatch : BytecodePatch(
    listOf(
        AboutPageFingerprint,
        AdPersonalizationActivityOnCreateFingerprint,
        SettingsOnViewCreatedFingerprint,
    )
) {
    override suspend fun execute(context: BytecodeContext) {
        SettingsOnViewCreatedFingerprint.result?.mutableMethod?.apply {
            val instructions = implementation!!.instructions

            // Find the indices that need to be patched.
            val copyrightPolicyLabelId = AboutPageFingerprint.result?.let {
                val startIndex = it.scanResult.patternScanResult!!.startIndex
                it.mutableMethod.getInstruction<WideLiteralInstruction>(startIndex).wideLiteral
            } ?: AboutPageFingerprint.error()

            val copyrightIndex = instructions.indexOfFirst {
                (it as? ReferenceInstruction)?.reference.toString() == "copyright_policy"
            } - 6


            // fixme: instead use Method.indexOfFirstConstantInstructionValue()
            val copyrightPolicyIndex = instructions.indexOfFirst {
                (it as? WideLiteralInstruction)?.wideLiteral == copyrightPolicyLabelId
            } + 2

            // Replace an existing settings entry with ReVanced settings entry.
            arrayOf(
                copyrightIndex,
                copyrightPolicyIndex
            ).forEach { index ->
                val instruction = getInstruction(index)
                if (instruction.opcode != Opcode.MOVE_RESULT_OBJECT)
                    throw PatchException("Hardcoded offset changed.")

                val settingsEntryStringRegister = (instruction as OneRegisterInstruction).registerA

                // Replace the settings entry string with a custom one.
                replaceInstruction(
                    index,
                    """
                    const-string v$settingsEntryStringRegister, "ReVanced Settings"
                """
                )

                // Replace the OnClickListener class with a custom one.
                val onClickListener = getInstruction<ReferenceInstruction>(index + 4).reference.toString()

                context.classes.findClassProxied(onClickListener)?.mutableClass?.methods?.first {
                    it.name == "onClick"
                }?.addInstructions(
                    0,
                    """
                        invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->startSettingsActivity()V
                        return-void
                    """
                ) ?: throw PatchException("Could not find the onClick method.")
            }

        } ?: SettingsOnViewCreatedFingerprint.error()

        // Initialize the settings menu once the replaced setting entry is clicked.
        AdPersonalizationActivityOnCreateFingerprint.result?.mutableMethod?.apply {
            val initializeSettingsIndex = implementation!!.instructions.indexOfFirst {
                it.opcode == Opcode.INVOKE_SUPER
            } + 1

            val thisRegister = getInstruction<FiveRegisterInstruction>(initializeSettingsIndex - 1).registerC

            addInstructions(
                initializeSettingsIndex,
                """
                    invoke-static {v$thisRegister}, $INITIALIZE_SETTINGS_METHOD_DESCRIPTOR
                    return-void
                """
            )
        } ?: AdPersonalizationActivityOnCreateFingerprint.error()
    }

    private companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/tiktok/settingsmenu/SettingsMenu;"

        private const val INITIALIZE_SETTINGS_METHOD_DESCRIPTOR =
            "$INTEGRATIONS_CLASS_DESCRIPTOR->initializeSettings(" +
                    "Lcom/bytedance/ies/ugc/aweme/commercialize/compliance/personalization/AdPersonalizationActivity;" +
                    ")V"
    }
}