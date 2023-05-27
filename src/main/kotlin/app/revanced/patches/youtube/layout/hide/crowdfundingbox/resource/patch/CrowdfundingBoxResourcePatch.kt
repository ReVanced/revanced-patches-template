package app.revanced.patches.youtube.layout.hide.crowdfundingbox.resource.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.crowdfundingbox.annotations.CrowdfundingBoxCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.resourceIdOf

@Name("crowdfunding-box-resource-patch")
@CrowdfundingBoxCompatibility
@DependsOn([SettingsPatch::class])
@Version("0.0.1")
class CrowdfundingBoxResourcePatch : ResourcePatch {
    companion object {
        internal var crowdfundingBoxId: Long = -1
    }

    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_crowdfunding_box",
                StringResource("revanced_hide_crowdfunding_box_title", "Hide crowdfunding box"),
                StringResource("revanced_hide_crowdfunding_box_summary_on", "Crowdfunding box is hidden"),
                StringResource("revanced_hide_crowdfunding_box_summary_off", "Crowdfunding box is shown")
            )
        )

        crowdfundingBoxId = context.resourceIdOf("layout", "donation_companion")

    }
}