package app.revanced.patches.youtube.misc.integrations

import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.integrations.patch.AbstractIntegrationsPatch
import app.revanced.patches.youtube.misc.integrations.fingerprints.*

@Patch(requiresIntegrations = true)
class IntegrationsPatch : AbstractIntegrationsPatch(
    "Lapp/revanced/integrations/utils/ReVancedUtils;",
    listOf(
        ApplicationInitFingerprint,
        StandalonePlayerActivityFingerprint,
        RemoteEmbeddedPlayerFingerprint,
        RemoteEmbedFragmentFingerprint,
        EmbeddedPlayerControlsOverlayFingerprint,
        EmbeddedPlayerFingerprint,
        APIPlayerServiceFingerprint,
    ),
)