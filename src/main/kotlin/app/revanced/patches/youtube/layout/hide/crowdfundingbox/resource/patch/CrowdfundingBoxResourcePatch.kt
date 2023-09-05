package app.revanced.patches.youtube.layout.hide.crowdfundingbox.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@DependsOn([SettingsPatch::class, ResourceMappingPatch::class])
class CrowdfundingBoxResourcePatch : ResourcePatch {
    companion object {
        internal var crowdfundingBoxId: Long = -1
    }

    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_crowdfunding_box",
                "revanced_hide_crowdfunding_box_title",
                "revanced_hide_crowdfunding_box_summary_on",
                "revanced_hide_crowdfunding_box_summary_off"
            )
        )

        crowdfundingBoxId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "donation_companion"
        }.id
    }
}