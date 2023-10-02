package app.revanced.patches.youtube.layout.hide.crowdfundingbox

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.patches.youtube.misc.settings.SettingsResourcePatch

@Patch(
    dependencies = [
        SettingsPatch::class,
        ResourceMappingPatch::class
    ],
)
object CrowdfundingBoxResourcePatch : ResourcePatch() {
    internal var crowdfundingBoxId: Long = -1

    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_crowdfunding_box",
                "revanced_hide_crowdfunding_box_title",
                "revanced_hide_crowdfunding_box_summary_on",
                "revanced_hide_crowdfunding_box_summary_off"
            )
        )
        SettingsResourcePatch.mergePatchStrings("CrowdfundingBox")

        crowdfundingBoxId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "donation_companion"
        }.id
    }
}