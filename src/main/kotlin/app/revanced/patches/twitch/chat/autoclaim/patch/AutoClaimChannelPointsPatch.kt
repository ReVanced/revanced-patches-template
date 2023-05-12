package app.revanced.patches.twitch.chat.autoclaim.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.*
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.twitch.chat.autoclaim.annotations.AutoClaimChannelPointsCompatibility
import app.revanced.patches.twitch.chat.autoclaim.fingerprints.*
import app.revanced.patches.twitch.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.twitch.misc.settings.bytecode.patch.SettingsPatch

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("auto-claim-channel-points")
@Description("Automatically claim Channel Points.")
@AutoClaimChannelPointsCompatibility
@Version("0.0.1")
class AutoClaimChannelPointPatch : BytecodePatch(
    listOf(
        CommunityPointsButtonViewDelegateFingerprint,
    )
) {
    private fun createAutoClaimConditionInstructions(register: String = "v0") = """
        invoke-static {}, Lapp/revanced/twitch/patches/AutoClaimChannelPointsPatch;->shouldAutoClaim()Z
        move-result $register
        if-eqz $register, :auto_claim
    """

    override fun execute(context: BytecodeContext): PatchResult {
        with(CommunityPointsButtonViewDelegateFingerprint.result!!.mutableMethod) {
            addInstructions(
                implementation!!.instructions.lastIndex, // place in front of return-void
                """
                    ${createAutoClaimConditionInstructions()}
                    iget-object v0, p0, Ltv/twitch/android/shared/community/points/viewdelegate/CommunityPointsButtonViewDelegate;->buttonLayout:Landroid/view/ViewGroup;
                    invoke-virtual { v0 }, Landroid/view/View;->callOnClick()Z
                """,
                listOf(ExternalLabel("auto_claim", instruction(implementation!!.instructions.lastIndex)))
            )
        }

        SettingsPatch.PreferenceScreen.CHAT.GENERAL.addPreferences(
            SwitchPreference(
                "revanced_auto_claim_channel_points",
                StringResource(
                    "revanced_auto_claim_channel_points",
                    "Automatically claim Channel Points"
                ),
                true,
                StringResource(
                    "revanced_auto_claim_channel_points_on",
                    "Auto claim is enabled"
                ),
                StringResource(
                    "revanced_auto_claim_channel_points_off",
                    "Auto claim is disabled"
                ),
            )
        )

        return PatchResultSuccess()
    }
}
