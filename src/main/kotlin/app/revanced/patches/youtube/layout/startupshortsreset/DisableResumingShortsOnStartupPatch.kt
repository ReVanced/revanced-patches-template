package app.revanced.patches.youtube.layout.startupshortsreset

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.startupshortsreset.fingerprints.UserWasInShortsFingerprint
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.strings.StringsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch

@Patch(
    name = "Disable resuming Shorts on startup",
    description = "Disables resuming the Shorts player on app startup if a Short was last opened.",
    dependencies = [IntegrationsPatch::class, SettingsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube", [
                "18.32.39",
                "18.37.36",
                "18.38.44",
                "18.43.45",
                "18.44.41",
                "18.45.41",
                "18.45.43"
            ]
        )
    ]
)
@Suppress("unused")
object DisableResumingShortsOnStartupPatch : BytecodePatch(
    setOf(UserWasInShortsFingerprint)
) {

    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/patches/DisableResumingStartupShortsPlayerPatch;"

    override fun execute(context: BytecodeContext) {
        StringsPatch.includePatchStrings("DisableResumingShortsOnStartup")
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_disable_resuming_shorts_player",
                "revanced_disable_resuming_shorts_player_title",
                "revanced_disable_resuming_shorts_player_summary_on",
                "revanced_disable_resuming_shorts_player_summary_off"
            )
        )

        UserWasInShortsFingerprint.result?.apply {
            val moveResultIndex = scanResult.patternScanResult!!.endIndex

            mutableMethod.addInstructionsWithLabels(
                moveResultIndex + 1,
                """
                invoke-static { }, $INTEGRATIONS_CLASS_DESCRIPTOR->disableResumingStartupShortsPlayer()Z
                move-result v5
                if-eqz v5, :disable_shorts_player
                return-void
                :disable_shorts_player
                nop
            """
            )
        } ?: throw UserWasInShortsFingerprint.exception
    }
}
