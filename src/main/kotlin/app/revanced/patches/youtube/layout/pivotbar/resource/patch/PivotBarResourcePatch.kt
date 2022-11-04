package app.revanced.patches.youtube.layout.pivotbar.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.youtube.layout.hidecrowdfundingbox.annotations.CrowdfundingBoxCompatibility
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Name("pivot-bar-resource-patch")
@CrowdfundingBoxCompatibility
@DependsOn([SettingsPatch::class, ResourceMappingResourcePatch::class])
@Version("0.0.1")
class PivotBarResourcePatch : ResourcePatch {
    companion object {
        internal var imageOnlyTabId: Long = -1
    }

    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_shorts_button_enabled",
                StringResource("revanced_shorts_button_enabled_title", "Show shorts button"),
                false,
                StringResource("revanced_shorts_button_summary_on", "Shorts button is shown"),
                StringResource("revanced_shorts_button_summary_off", "Shorts button is hidden")
            )
        )

        imageOnlyTabId = ResourceMappingResourcePatch.resourceMappings.single {
            it.type == "layout" && it.name == "image_only_tab"
        }.id

        return PatchResultSuccess()
    }
}