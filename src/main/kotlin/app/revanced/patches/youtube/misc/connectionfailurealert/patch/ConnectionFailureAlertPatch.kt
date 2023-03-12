package app.revanced.patches.youtube.misc.connectionfailurealert.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.connectionfailurealert.annotations.ConnectionFailureAlertCompatibility
import app.revanced.patches.youtube.misc.connectionfailurealert.fingerprints.ConnectionFailureAlertFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class])
@Name("connection-failure-alert")
@Description("Detects video playback issues and displays a user alert")
@ConnectionFailureAlertCompatibility
@Version("0.0.1")
class ConnectionFailureAlertPatch : BytecodePatch(
    listOf(ConnectionFailureAlertFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = ConnectionFailureAlertFingerprint.result!!
        val method = result.mutableMethod

        val endIndex = result.scanResult.patternScanResult!!.endIndex
        val statusCodeRegister = (method.instruction(endIndex - 2) as OneRegisterInstruction).registerA
        val urlHeadersRegister = (method.instruction(endIndex) as OneRegisterInstruction).registerA

        method.addInstructions(
            endIndex + 1,
            """
                invoke-static {v$statusCodeRegister, v$urlHeadersRegister}, $INTEGRATIONS_PATCH_CLASS_DESCRIPTOR->connectionCompleted(ILjava/util/Map;)V
            """
        )

        return PatchResultSuccess()
    }

    private companion object {
        const val INTEGRATIONS_PATCH_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/ConnectionFailureAlertPatch;"
    }
}
