package app.revanced.patches.youtube.layout.panels.fullscreen.remove.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.panels.fullscreen.remove.annotations.FullscreenPanelsCompatibility
import app.revanced.patches.youtube.layout.panels.fullscreen.remove.fingerprints.FullscreenViewAdderFingerprint
import app.revanced.patches.youtube.layout.panels.fullscreen.remove.fingerprints.FullscreenViewAdderParentFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@Name("disable-fullscreen-panels")
@DependsOn([IntegrationsPatch::class, YouTubeSettingsPatch::class])
@Description("Disables video description and comments panel in fullscreen view.")
@FullscreenPanelsCompatibility
@Version("0.0.1")
class FullscreenPanelsRemoverPatch : BytecodePatch(
    listOf(
        FullscreenViewAdderParentFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_fullscreen_panels",
                "revanced_hide_fullscreen_panels_title",
                "revanced_hide_fullscreen_panels_summary_on",
                "revanced_hide_fullscreen_panels_summary_off"
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
            ifIndex, """
            invoke-static {}, Lapp/revanced/integrations/patches/FullscreenPanelsRemoverPatch;->getFullscreenPanelsVisibility()I
            move-result p1
        """
        )

        return PatchResultSuccess()
    }
}
