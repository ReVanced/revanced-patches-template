package app.revanced.patches.twitch.chat.autoclaim

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.twitch.chat.autoclaim.fingerprints.CommunityPointsButtonViewDelegateFingerprint
import app.revanced.patches.twitch.misc.strings.StringsPatch
import app.revanced.patches.twitch.misc.settings.SettingsPatch

@Patch(
    name = "Auto claim channel points",
    description = "Automatically claim Channel Points.",
    dependencies = [SettingsPatch::class],
    compatiblePackages = [CompatiblePackage("tv.twitch.android.app", ["15.4.1", "16.1.0", "17.0.0", "17.1.0"])]
)
@Suppress("unused")
object AutoClaimChannelPointPatch : BytecodePatch(
    setOf(CommunityPointsButtonViewDelegateFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        StringsPatch.includePatchStrings("AutoClaimChannelPoint")
        SettingsPatch.PreferenceScreen.CHAT.GENERAL.addPreferences(
            SwitchPreference(
                "revanced_auto_claim_channel_points",
                "revanced_auto_claim_channel_points",
                "revanced_auto_claim_channel_points_on",
                "revanced_auto_claim_channel_points_off",
                default = true
            )
        )

        CommunityPointsButtonViewDelegateFingerprint.result?.mutableMethod?.apply {
            val lastIndex = implementation!!.instructions.lastIndex
            addInstructionsWithLabels(
                lastIndex, // place in front of return-void
                """
                    invoke-static {}, Lapp/revanced/twitch/patches/AutoClaimChannelPointsPatch;->shouldAutoClaim()Z
                    move-result v0
                    if-eqz v0, :auto_claim

                    # Claim by calling the button's onClick method

                    iget-object v0, p0, Ltv/twitch/android/shared/community/points/viewdelegate/CommunityPointsButtonViewDelegate;->buttonLayout:Landroid/view/ViewGroup;
                    invoke-virtual { v0 }, Landroid/view/View;->callOnClick()Z
                """,
                ExternalLabel("auto_claim", getInstruction(lastIndex))
            )
        } ?: throw CommunityPointsButtonViewDelegateFingerprint.exception
    }
}
