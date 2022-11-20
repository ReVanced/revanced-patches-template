package app.revanced.patches.tiktok.misc.settings.patch

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
import app.revanced.patches.tiktok.misc.integrations.patch.TikTokIntegrationsPatch
import app.revanced.patches.tiktok.misc.settings.annotations.TikTokSettingsCompatibility
import app.revanced.patches.tiktok.misc.settings.fingerprints.AboutOnClickMethodFingerprint
import app.revanced.patches.tiktok.misc.settings.fingerprints.AdPersonalizationActivityOnCreateFingerprint
import app.revanced.patches.tiktok.misc.settings.fingerprints.SettingsOnViewCreatedFingerprint
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.reference.StringReference
import org.jf.dexlib2.iface.reference.TypeReference

@Patch
@DependsOn([TikTokIntegrationsPatch::class])
@Name("settings")
@Description("Adds settings to TikTok.")
@TikTokSettingsCompatibility
@Version("0.0.1")
class TikTokSettingsPatch : BytecodePatch(
    listOf(
        AdPersonalizationActivityOnCreateFingerprint,
        SettingsOnViewCreatedFingerprint,
        AboutOnClickMethodFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        //Patch Tiktok Settings UI to add 'Revanced Settings'.
        val targetIndexes = findOptionsOnClickIndex()
        with(SettingsOnViewCreatedFingerprint.result!!.mutableMethod) {
            val instructions = implementation!!.instructions
            for (index in targetIndexes) {
                if (
                    instructions[index].opcode != Opcode.NEW_INSTANCE ||
                    instructions[index - 4].opcode != Opcode.MOVE_RESULT_OBJECT
                )
                    return PatchResultError("Hardcode offset changed.")
                patchOptionNameAndOnClickEvent(index, context)
            }
        }
        //Implement revanced settings screen in `AdPersonalizationActivity`
        with(AdPersonalizationActivityOnCreateFingerprint.result!!.mutableMethod) {
            for ((index, instruction) in implementation!!.instructions.withIndex()) {
                if (instruction.opcode != Opcode.INVOKE_SUPER) continue
                val thisRegister = (instruction as Instruction35c).registerC
                addInstructions(
                    index + 1,
                    """
                        invoke-static {v$thisRegister}, Lapp/revanced/tiktok/settingsmenu/SettingsMenu;->initializeSettings(Lcom/bytedance/ies/ugc/aweme/commercialize/compliance/personalization/AdPersonalizationActivity;)V
                        return-void
                    """
                )
                break
            }
        }
        return PatchResultSuccess()
    }

    private fun findOptionsOnClickIndex(): IntArray {
        val results = IntArray(2)
        var found = 0
        with(SettingsOnViewCreatedFingerprint.result!!.mutableMethod) {
            val instructions = implementation!!.instructions

            for ((index, instruction) in instructions.withIndex()) {
                //OldUI settings option to replace to 'Revanced Settings'
                if (instruction.opcode == Opcode.CONST_STRING) {
                    val string = ((instruction as ReferenceInstruction).reference as StringReference).string
                    if (string == "copyright_policy") {
                        results[0] = index - 2
                        found++
                    }
                }
                //NewUI settings option to replace to 'Revanced Settings'
                if (instruction.opcode == Opcode.NEW_INSTANCE) {
                    val onClickClass = ((instruction as Instruction21c).reference as TypeReference).type
                    if (onClickClass == AboutOnClickMethodFingerprint.result!!.mutableMethod.definingClass) {
                        results[1] = index
                        found++
                    }
                }
                if (found > 1) break
            }
        }
        return results
    }

    private fun patchOptionNameAndOnClickEvent(index: Int, context: BytecodeContext) {
        with(SettingsOnViewCreatedFingerprint.result!!.mutableMethod) {
            val instructions = implementation!!.instructions
            //Patch option name
            val stringInstruction = instructions[index - 4]
            val overrideRegister = (stringInstruction as OneRegisterInstruction).registerA
            replaceInstruction(
                index - 4,
                """
                        const-string v$overrideRegister, "Revanced Settings"
                    """
            )
            //Patch option OnClick Event
            val onClickInstruction = instruction(index)
            with(((onClickInstruction as ReferenceInstruction).reference as TypeReference).type) {
                context.findClass(this)!!
                    .mutableClass
                    .methods.first { it.name == "onClick" }.addInstructions(
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