package app.revanced.patches.youtube.misc.privacy

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch

import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.privacy.fingerprints.CopyTextFingerprint
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.patches.youtube.misc.strings.StringsPatch
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

@Patch(
    name = "Remove tracking query parameter",
    description = "Remove the tracking query parameter from links.",
    dependencies = [IntegrationsPatch::class, SettingsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.43.45",
                "18.44.41",
            ]
        )
    ]
)
@Suppress("unused")
object RemoveTrackingQueryParameterPatch : BytecodePatch(
    setOf(CopyTextFingerprint)
) {
    const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/RemoveTrackingQueryParameterPatch;"

    override fun execute(context: BytecodeContext) {
        StringsPatch.includePatchStrings("RemoveTrackingQueryParameter")
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_remove_tracking_query_parameter",
                "revanced_remove_tracking_query_parameter_title",
                "revanced_remove_tracking_query_parameter_summary_on",
                "revanced_remove_tracking_query_parameter_summary_off",
            )
        )

        CopyTextFingerprint.result?.let {
            val insertIndex = it.scanResult.patternScanResult!!.startIndex + 2

            it.mutableMethod.apply {
                val urlRegister = getInstruction<TwoRegisterInstruction>(insertIndex - 2).registerA

                addInstructions(
                    insertIndex,
                    """
                        invoke-static {v$urlRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->sanitize(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$urlRegister
                    """
                )
            }

        }
    }
}