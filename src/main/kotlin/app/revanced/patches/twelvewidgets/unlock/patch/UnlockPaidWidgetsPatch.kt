package app.revanced.patches.twelvewidgets.unlock.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.twelvewidgets.unlock.annotations.DetectionCompatibility
import app.revanced.patches.twelvewidgets.unlock.fingerprints.*

@Patch
@Name("unlock-paid-widgets")
@Description("Unlocks paid widgets of the app")
@DetectionCompatibility
@Version("0.0.1")
class UnlockPaidWidgetsPatch : BytecodePatch(
    listOf(
        AgendaDaysWidgetUnlockFingerprint,
        CalendarBigWidgetUnlockFingerprint,
        CalendarWideTimelineWidgetConfigureActivity,
        ScreentimeSmallWidgetConfigureActivity,
        WeatherWidgetConfigureActivity
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        listOf(
            AgendaDaysWidgetUnlockFingerprint,
            CalendarBigWidgetUnlockFingerprint,
            CalendarWideTimelineWidgetConfigureActivity,
            ScreentimeSmallWidgetConfigureActivity,
            WeatherWidgetConfigureActivity
        ).forEach { fingerprint ->
            if (fingerprint.result == null) return PatchResultError("Couldn't find method to patch")
            val mutableMethod = fingerprint.result!!.mutableMethod

            mutableMethod.removeInstructions(4, 2)
            mutableMethod.addInstructions(mutableMethod.implementation?.instructions?.size?.minus(1)!!,
                """
                    check-cast v0, Landroid/widget/Button;
                    const/4 v1, 0x0
                    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V
                """
            )
        }

        return PatchResultSuccess()
    }
}
