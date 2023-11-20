package app.revanced.patches.youtube.layout.buttons.autoplay

import app.revanced.extensions.exception
import app.revanced.extensions.findIndexForIdResource
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.strings.StringsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.patches.youtube.shared.fingerprints.LayoutConstructorFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Patch(
    name = "Hide autoplay button",
    description = "Hides the autoplay button in the video player.",
    dependencies = [
        IntegrationsPatch::class,
        SettingsPatch::class,
        ResourceMappingPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.16.37",
                "18.19.35",
                "18.20.39",
                "18.23.35",
                "18.29.38",
                "18.32.39",
                "18.37.36",
                "18.38.44",
                "18.43.45",
                "18.44.41",
                "18.45.41"
            ]
        )
    ]
)
@Suppress("unused")
object HideAutoplayButtonPatch : BytecodePatch(
    setOf(LayoutConstructorFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        StringsPatch.includePatchStrings("HideAutoplayButton")
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_autoplay_button",
                "revanced_hide_autoplay_button_title",
                "revanced_hide_autoplay_button_summary_on",
                "revanced_hide_autoplay_button_summary_off"
            ),
        )

        LayoutConstructorFingerprint.result?.mutableMethod?.apply {
            val layoutGenMethodInstructions = implementation!!.instructions

            // resolve the offsets of where to insert the branch instructions and ...
            val insertIndex = findIndexForIdResource("autonav_preview_stub")

            // where to branch away
            val branchIndex =
                layoutGenMethodInstructions.subList(
                    insertIndex + 1,
                    layoutGenMethodInstructions.size - 1
                )
                    .indexOfFirst {
                        ((it as? ReferenceInstruction)?.reference as? MethodReference)?.name == "addOnLayoutChangeListener"
                    } + 2

            val jumpInstruction =
                layoutGenMethodInstructions[insertIndex + branchIndex] as Instruction

            // can be clobbered because this register is overwritten after the injected code
            val clobberRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

            addInstructionsWithLabels(
                insertIndex,
                """
                    invoke-static {}, Lapp/revanced/integrations/patches/HideAutoplayButtonPatch;->isButtonShown()Z
                    move-result v$clobberRegister
                    if-eqz v$clobberRegister, :hidden
                """,
                ExternalLabel("hidden", jumpInstruction)
            )
        } ?: throw LayoutConstructorFingerprint.exception
    }
}