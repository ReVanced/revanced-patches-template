package app.revanced.patches.youtube.layout.hide.getpremium.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.getpremium.annotations.HideGetPremiumVideoAdvertisementCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@Name("hide-get-premium-resource-patch")
@DependsOn([SettingsPatch::class])
class HideGetPremiumVideoAdvertisementResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_get_premium",
                StringResource("revanced_hide_get_premium_title", "Hide YouTube Premium advertisement"),
                true,
                StringResource("revanced_hide_get_premium_summary_on", "YouTube Premium advertisement are hidden"),
                StringResource("revanced_hide_get_premium_summary_off", "YouTube Premium advertisement are shown")
            )
        )

        return PatchResultSuccess()
    }
}