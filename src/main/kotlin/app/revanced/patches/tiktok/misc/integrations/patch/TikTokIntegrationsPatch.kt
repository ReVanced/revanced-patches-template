package app.revanced.patches.tiktok.misc.integrations.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.patch.annotations.RequiresIntegrations
import app.revanced.patches.shared.integrations.patch.AbstractIntegrationsPatch
import app.revanced.patches.tiktok.misc.integrations.fingerprints.InitFingerprint

@Name("Integrations")
@RequiresIntegrations
class TikTokIntegrationsPatch : AbstractIntegrationsPatch(
    "Lapp/revanced/tiktok/utils/TikTokUtils;",
    listOf(InitFingerprint)
)