package app.revanced.patches.youtube.layout.hide.personalinformation.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.personalinformation.annotations.HideEmailAddressCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Name("hide-email-address-resource-patch")
@HideEmailAddressCompatibility
@DependsOn([YouTubeSettingsPatch::class, ResourceMappingPatch::class])
@Version("0.0.1")
class HideEmailAddressResourcePatch : ResourcePatch {
    companion object {
        internal var accountSwitcherAccessibilityLabelId: Long = -1
    }

    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_email_address",
                "revanced_hide_email_address_title",
                "revanced_hide_email_address_summary_on",
                "revanced_hide_email_address_summary_off"
            )
        )

        accountSwitcherAccessibilityLabelId = ResourceMappingPatch.resourceMappings.single {
            it.type == "string" && it.name == "account_switcher_accessibility_label"
        }.id

        return PatchResultSuccess()
    }
}