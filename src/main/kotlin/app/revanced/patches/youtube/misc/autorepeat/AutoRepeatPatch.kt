package app.revanced.patches.youtube.misc.autorepeat

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.autorepeat.fingerprints.AutoRepeatFingerprint
import app.revanced.patches.youtube.misc.autorepeat.fingerprints.AutoRepeatParentFingerprint
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch


@Patch(
    name = "Always autorepeat",
    description = "Always repeats the playing video again.",
    dependencies = [IntegrationsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.16.37",
                "18.19.35",
                "18.20.39",
                "18.23.35",
                "18.29.38",
                "18.32.39"
            ]
        )
    ]
)
@Suppress("unused")
object AutoRepeatPatch : BytecodePatch(
    setOf(AutoRepeatParentFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_auto_repeat",
                StringResource("revanced_auto_repeat_title", "Enable auto-repeat"),
                StringResource("revanced_auto_repeat_summary_on", "Auto-repeat is enabled"),
                StringResource("revanced_auto_repeat_summary_off", "Auto-repeat is disabled")
            )
        )

        //Get Result from the ParentFingerprint which is the playMethod we need to get.
        val parentResult = AutoRepeatParentFingerprint.result
            ?: throw PatchException("ParentFingerprint did not resolve.")

        //this one needs to be called when app/revanced/integrations/patches/AutoRepeatPatch;->shouldAutoRepeat() returns true
        val playMethod = parentResult.mutableMethod
        AutoRepeatFingerprint.resolve(context, parentResult.classDef)
        //String is: Laamp;->E()V
        val methodToCall = playMethod.definingClass + "->" + playMethod.name + "()V"

        //This is the method we search for
        val result = AutoRepeatFingerprint.result
            ?: throw PatchException("FingerPrint did not resolve.")
        val method = result.mutableMethod

        //Instructions to add to the smali code
        val instructions = """
            invoke-static {}, Lapp/revanced/integrations/patches/AutoRepeatPatch;->shouldAutoRepeat()Z
            move-result v0
            if-eqz v0, :noautorepeat
            invoke-virtual {p0}, $methodToCall
            :noautorepeat
            return-void
        """

        //Get the implementation so we can do a check for null and get instructions size.
        val implementation = method.implementation
            ?: throw PatchException("No Method Implementation found!")

        //Since addInstructions needs an index which starts counting at 0 and size starts counting at 1,
        //we have to remove 1 to get the latest instruction
        val index = implementation.instructions.size - 1


        //remove last instruction which is return-void
        method.removeInstruction(index)
        // Add our own instructions there
        method.addInstructionsWithLabels(index, instructions)

        //Everything worked as expected, return Success
    }
}
