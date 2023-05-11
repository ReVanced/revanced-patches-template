package app.revanced.patches.twitch.chat.autoclaim.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.*
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.settings.preference.impl.ArrayResource
import app.revanced.patches.shared.settings.preference.impl.ListPreference
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.twitch.chat.autoclaim.annotations.AutoClaimCommunityPointsCompatibility
import app.revanced.patches.twitch.chat.autoclaim.fingerprints.*

@Patch
@Name("auto-claim-community-points")
@Description("Automatically claim Community points.")
@AutoClaimCommunityPointsCompatibility
@Version("0.0.1")
class AutoClaimCommunityPointPatch : BytecodePatch(
    listOf(
        CommunityPointsButtonViewDelegateFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        with(CommunityPointsButtonViewDelegateFingerprint.result!!.mutableMethod) {
            addInstructions(
                implementation!!.instructions.lastIndex, // place in front of return-void
                """
                    iget-object v0, p0, Ltv/twitch/android/shared/community/points/viewdelegate/CommunityPointsButtonViewDelegate;->buttonLayout:Landroid/view/ViewGroup;
                    invoke-virtual { v0 }, Landroid/view/View;->callOnClick()Z
                """
            )
        }

        return PatchResultSuccess()
    }
}
