package app.revanced.patches.youtube.layout.tabletminiplayer.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.tabletminiplayer.annotations.TabletMiniPlayerCompatibility
import app.revanced.patches.youtube.layout.tabletminiplayer.fingerprints.*
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("tablet-mini-player")
@Description("Enables the tablet mini player layout.")
@TabletMiniPlayerCompatibility
@Version("0.0.1")
class TabletMiniPlayerPatch : BytecodePatch(
    listOf(
        MiniPlayerDimensionsCalculatorParentFingerprint,
        MiniPlayerResponseModelSizeCheckFingerprint,
        MiniPlayerOverrideParentFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_tablet_miniplayer",
                StringResource("revanced_tablet_miniplayer_title", "Enable tablet mini player"),
                StringResource("revanced_tablet_miniplayer_summary_on", "Mini player is enabled"),
                StringResource("revanced_tablet_miniplayer_summary_off", "Mini player is disabled")
            )
        )

        // First resolve the fingerprints via the parent fingerprint.
        MiniPlayerDimensionsCalculatorParentFingerprint.result ?: return MiniPlayerDimensionsCalculatorParentFingerprint.toErrorResult()
        val miniPlayerClass = MiniPlayerDimensionsCalculatorParentFingerprint.result!!.classDef

        /*
         * No context parameter method.
         */
        MiniPlayerOverrideNoContextFingerprint.resolve(context, miniPlayerClass)
        val (method, _, parameterRegister) = MiniPlayerOverrideNoContextFingerprint.addProxyCall()

        // Insert right before the return instruction.
        val secondInsertIndex = method.implementation!!.instructions.size - 1
        method.insertOverride(
            secondInsertIndex, parameterRegister
            /** same register used to return **/
        )

        /*
         * Method with context parameter.
         */
        MiniPlayerOverrideParentFingerprint.result?.let {
            if (!MiniPlayerOverrideFingerprint.resolve(context, it.classDef))
                throw MiniPlayerOverrideFingerprint.toErrorResult()
        } ?: return MiniPlayerOverrideParentFingerprint.toErrorResult()

        /*
         * Override every return instruction with the proxy call.
         */
        MiniPlayerOverrideFingerprint.result!!.mutableMethod.apply {
            implementation!!.let { implementation ->
                val returnIndices = implementation.instructions
                    .withIndex()
                    .filter { (_, instruction) -> instruction.opcode == Opcode.RETURN }
                    .map { (index, _) -> index }

                if (returnIndices.isEmpty()) throw PatchResultError("No return instructions found.")

                // This method clobbers register p0 to return the value, calculate to override.
                val returnedRegister = implementation.registerCount - parameters.size

                // Hook the returned register on every return instruction.
                returnIndices.forEach { index -> insertOverride(index, returnedRegister) }
            }
        }

        /*
         * Size check return value override.
         */
        MiniPlayerResponseModelSizeCheckFingerprint.addProxyCall()

        return PatchResultSuccess()
    }

    // Helper methods.
    private companion object {
        fun MethodFingerprint.addProxyCall(): Triple<MutableMethod, Int, Int> {
            val (method, scanIndex, parameterRegister) = this.unwrap()
            method.insertOverride(scanIndex, parameterRegister)

            return Triple(method, scanIndex, parameterRegister)
        }

        fun MutableMethod.insertOverride(index: Int, overrideRegister: Int) {
            this.addInstructions(
                index,
                """
                    invoke-static {v$overrideRegister}, Lapp/revanced/integrations/patches/TabletMiniPlayerOverridePatch;->getTabletMiniPlayerOverride(Z)Z
                    move-result v$overrideRegister
                """
            )
        }

        fun MethodFingerprint.unwrap(): Triple<MutableMethod, Int, Int> {
            val result = this.result!!
            val scanIndex = result.scanResult.patternScanResult!!.endIndex
            val method = result.mutableMethod
            val instructions = method.implementation!!.instructions
            val parameterRegister = (instructions[scanIndex] as OneRegisterInstruction).registerA

            return Triple(method, scanIndex, parameterRegister)
        }
    }
}
