package app.revanced.patches.youtube.layout.panels.fullscreen.remove.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.panels.fullscreen.remove.annotations.FullscreenPanelsCompatibility
import app.revanced.patches.youtube.layout.panels.fullscreen.remove.fingerprints.FullscreenViewAdderFingerprint
import app.revanced.patches.youtube.layout.panels.fullscreen.remove.fingerprints.FullscreenViewAdderParentFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@Patch
@Name("disable-fullscreen-panels")
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Description("Disables video description and comments panel in fullscreen view.")
@FullscreenPanelsCompatibility
@Version("0.0.1")
class FullscreenPanelsRemoverPatch : BytecodePatch(
    listOf(
        FullscreenViewAdderParentFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_fullscreen_panels",
                StringResource("revanced_hide_fullscreen_panels_title", "Hide fullscreen panels"),
                StringResource("revanced_hide_fullscreen_panels_summary_on", "Fullscreen panels are hidden"),
                StringResource("revanced_hide_fullscreen_panels_summary_off", "Fullscreen panels are shown")
            )
        )

        val parentResult = FullscreenViewAdderParentFingerprint.result!!
        FullscreenViewAdderFingerprint.resolve(context, parentResult.method, parentResult.classDef)
        val result = FullscreenViewAdderParentFingerprint.result
            ?: return PatchResultError("Fingerprint not resolved!")

        val method = result.mutableMethod

        val ifIndex = result.scanResult.patternScanResult!!.startIndex + 2

        method.removeInstruction(ifIndex)
        method.addInstructions(
            ifIndex,
            """
                invoke-static {}, Lapp/revanced/integrations/patches/FullscreenPanelsRemoverPatch;->getFullscreenPanelsVisibility()I
                move-result p1
            """
        )

        return PatchResultSuccess()
    }
}
