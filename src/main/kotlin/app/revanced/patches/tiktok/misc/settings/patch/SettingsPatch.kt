package app.revanced.patches.tiktok.misc.settings.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.tiktok.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.tiktok.misc.settings.annotations.SettingsCompatibility
import app.revanced.patches.tiktok.misc.settings.fingerprints.AboutPageFingerprint
import app.revanced.patches.tiktok.misc.settings.fingerprints.AdPersonalizationActivityOnCreateFingerprint
import app.revanced.patches.tiktok.misc.settings.fingerprints.SettingsOnViewCreatedFingerprint
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.reference.TypeReference

@Patch
@DependsOn([IntegrationsPatch::class])
@Name("settings")
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
    override fun execute(context: BytecodeContext): PatchResult {

        AboutPageFingerprint.result?.let {
            val startIndex = it.scanResult.patternScanResult!!.startIndex

            copyrightPolicyLabelId = (it.mutableMethod.instruction(startIndex) as WideLiteralInstruction).wideLiteral

        } ?: return AboutPageFingerprint.toErrorResult()

        SettingsOnViewCreatedFingerprint.result?.mutableMethod?.let { method ->
            val copyrightInstructions = method.implementation!!.instructions

            val copyrightIndex = copyrightInstructions.indexOfFirst {
                it.opcode == Opcode.CONST_STRING &&
                        (it as BuilderInstruction21c).reference.toString() == "copyright_policy"
            } - 6

            val copyrightPolicyIndex = copyrightInstructions.indexOfFirst {
                it.opcode == Opcode.CONST &&
                        (it as WideLiteralInstruction).wideLiteral == copyrightPolicyLabelId
            } + 2

            arrayOf(
                copyrightIndex,
                copyrightPolicyIndex
            ).forEach {
                if (method.instruction(it).opcode != Opcode.MOVE_RESULT_OBJECT)
                    return PatchResultError("Hardcode offset changed.")
                val register = (method.instruction(it) as OneRegisterInstruction).registerA

                method.insertSettings(context, it, register)
            }

        } ?: return SettingsOnViewCreatedFingerprint.toErrorResult()

        AdPersonalizationActivityOnCreateFingerprint.result?.mutableMethod?.let {
            for ((index, instruction) in it.implementation!!.instructions.withIndex()) {
                if (instruction.opcode != Opcode.INVOKE_SUPER) continue
                val thisRegister = (instruction as Instruction35c).registerC
                it.addInstructions(
                    index + 1,
                    """
                        invoke-static {v$thisRegister}, Lapp/revanced/tiktok/settingsmenu/SettingsMenu;->initializeSettings(Lcom/bytedance/ies/ugc/aweme/commercialize/compliance/personalization/AdPersonalizationActivity;)V
                        return-void
                    """
                )
                return PatchResultSuccess()
            }
        } ?: return AdPersonalizationActivityOnCreateFingerprint.toErrorResult()

        return PatchResultError("Could not find the method to hook.")
    }

    private companion object {
        var copyrightPolicyLabelId: Long = -1

        fun MutableMethod.insertSettings(
            context: BytecodeContext,
            index: Int,
            overrideRegister: Int
        ) {
            replaceInstruction(
                index,
                """
                    const-string v$overrideRegister, "Revanced Settings"
                """
            )

            with(((instruction(index + 4) as ReferenceInstruction).reference as TypeReference).type) {
                context.findClass(this)!!.mutableClass.methods.first { it.name == "onClick" }
                    .addInstructions(
                        0,
                        """
                                 invoke-static {}, Lapp/revanced/tiktok/settingsmenu/SettingsMenu;->startSettingsActivity()V
                                 return-void
                             """
                    )
            }
        }
    }
}