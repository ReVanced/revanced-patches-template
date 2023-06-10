package app.revanced.patches.tiktok.misc.settings.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.*
import app.revanced.patches.tiktok.misc.integrations.patch.TikTokIntegrationsPatch
import app.revanced.patches.tiktok.misc.settings.annotations.TikTokSettingsCompatibility
import app.revanced.util.resources.ResourceUtils.mergeStrings

@Name("settings-resource-patch")
@TikTokSettingsCompatibility
@DependsOn([TikTokIntegrationsPatch::class])
@Version("0.0.1")
class TikTokSettingsResourcePatch : ResourcePatch {

    override fun execute(context: ResourceContext): PatchResult {
        context.mergeStrings("tiktok/settings/host/values/strings.xml")

        return PatchResultSuccess()
    }

}