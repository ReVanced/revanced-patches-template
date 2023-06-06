package app.revanced.patches.youtube.misc.zoomhaptics.patch

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
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch
import app.revanced.patches.youtube.misc.zoomhaptics.annotations.ZoomHapticsCompatibility
import app.revanced.patches.youtube.misc.zoomhaptics.fingerprints.ZoomHapticsFingerprint

@Patch
@Name("disable-zoom-haptics")
@Description("Disables haptics when zooming.")
@DependsOn([YouTubeSettingsPatch::class])
@ZoomHapticsCompatibility
@Version("0.0.1")
class ZoomHapticsPatch : BytecodePatch(
    listOf(ZoomHapticsFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_disable_zoom_haptics",
                "revanced_disable_zoom_haptics_title",
                "revanced_disable_zoom_haptics_summary_on",
                "revanced_disable_zoom_haptics_summary_off"
            )
        )

        val zoomHapticsFingerprintMethod = ZoomHapticsFingerprint.result!!.mutableMethod

        zoomHapticsFingerprintMethod.addInstructions(
            0, """
                invoke-static { }, Lapp/revanced/integrations/patches/ZoomHapticsPatch;->shouldVibrate()Z
                move-result v0
                if-nez v0, :vibrate
                return-void
            """, listOf(ExternalLabel("vibrate", zoomHapticsFingerprintMethod.instruction(0)))
        )

        return PatchResultSuccess()
    }
}