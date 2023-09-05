package app.revanced.patches.youtube.ad.general.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.ad.general.annotation.HideAdsCompatibility
import app.revanced.patches.youtube.misc.litho.filter.patch.LithoFilterPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch.PreferenceScreen

@DependsOn(
    [
        LithoFilterPatch::class,
        SettingsPatch::class,
        ResourceMappingPatch::class
    ]
)
@HideAdsCompatibility
class HideAdsResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        PreferenceScreen.ADS.addPreferences(
            SwitchPreference(
                "revanced_hide_general_ads",
                "revanced_hide_general_ads_title",
                "revanced_hide_general_ads_summary_on",
                "revanced_hide_general_ads_summary_off"
            ),
            SwitchPreference(
                "revanced_hide_buttoned_ads",
                "revanced_hide_buttoned_ads_title",
                "revanced_hide_buttoned_ads_summary_on",
                "revanced_hide_buttoned_ads_summary_off"
            ),
            SwitchPreference(
                "revanced_hide_paid_content_ads",
                "revanced_hide_paid_content_ads_title",
                "revanced_hide_paid_content_ads_summary_on",
                "revanced_hide_paid_content_ads_summary_off"
            ),
            SwitchPreference(
                "revanced_hide_self_sponsor_ads",
                "revanced_hide_self_sponsor_ads_title",
                "revanced_hide_self_sponsor_ads_summary_on",
                "revanced_hide_self_sponsor_ads_summary_off"
            ),
            SwitchPreference(
                "revanced_hide_products_banner",
                "revanced_hide_products_banner_title",
                "revanced_hide_products_banner_summary_on",
                "revanced_hide_products_banner_summary_off"
            ),
            SwitchPreference(
                "revanced_hide_web_search_results",
                "revanced_hide_web_search_results_title",
                "revanced_hide_web_search_results_summary_on",
                "revanced_hide_web_search_results_summary_off",
            ),
            SwitchPreference(
                "revanced_hide_merchandise_banners",
                "revanced_hide_merchandise_banners_title",
                "revanced_hide_merchandise_banners_summary_on",
                "revanced_hide_merchandise_banners_summary_off",
            )
        )

        LithoFilterPatch.addFilter(FILTER_CLASS_DESCRIPTOR)

        adAttributionId = ResourceMappingPatch.resourceMappings.single { it.name == "ad_attribution" }.id
    }

    internal companion object {
        var adAttributionId: Long = -1

        private const val FILTER_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/components/AdsFilter;"
    }
}
