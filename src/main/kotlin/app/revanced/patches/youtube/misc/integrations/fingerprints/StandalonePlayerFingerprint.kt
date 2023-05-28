package app.revanced.patches.youtube.misc.integrations.fingerprints

import app.revanced.patches.shared.integrations.patch.AbstractIntegrationsPatch.IntegrationsFingerprint

// Edit: It's not clear when this hook is used (if it is still used at all)
object StandalonePlayerFingerprint : IntegrationsFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass == "Lcom/google/android/youtube/api/StandalonePlayerActivity;"
                && methodDef.name == "onCreate"
    },
    // Integrations context is the Activity itself.
)