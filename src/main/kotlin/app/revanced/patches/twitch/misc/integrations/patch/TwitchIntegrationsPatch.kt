package app.revanced.patches.twitch.misc.integrations.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patches.twitch.misc.integrations.fingerprints.InitFingerprint
import app.revanced.patches.twitch.misc.integrations.annotations.TwitchIntegrationsCompatibility
import app.revanced.shared.patches.AbstractIntegrationsPatch

@Name("twitch-integrations")
@TwitchIntegrationsCompatibility
class TwitchIntegrationsPatch : AbstractIntegrationsPatch(
    "Lapp/revanced/tiktok/utils/ReVancedUtils;",
    listOf(InitFingerprint)
)