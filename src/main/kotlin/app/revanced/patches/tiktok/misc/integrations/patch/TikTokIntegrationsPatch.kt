package app.revanced.patches.tiktok.misc.integrations.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.tiktok.misc.integrations.annotations.TikTokIntegrationsCompatibility

@Name("tiktok-integrations")
@Description("Applies mandatory patches to implement the ReVanced integrations into the application.")
@TikTokIntegrationsCompatibility
@Version("0.0.1")
class TikTokIntegrationsPatch : BytecodePatch() {
    override fun execute(data: BytecodeData): PatchResult {
        if (data.findClass("Lapp/revanced/integrations/tiktok/FeedItemsFilter") == null)
            return PatchResultError("Integrations have not been merged yet. This patch can not succeed without the integrations.")
        return PatchResultSuccess()
    }
}