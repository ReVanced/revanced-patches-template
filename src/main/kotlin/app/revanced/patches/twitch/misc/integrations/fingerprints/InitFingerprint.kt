package app.revanced.patches.twitch.misc.integrations.fingerprints

import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.integrations.patch.AbstractIntegrationsPatch.IntegrationsFingerprint

@Patch(name = "Init fingerprint")
object InitFingerprint : IntegrationsFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("/TwitchApplication;") &&
                methodDef.name == "onCreate"
    }
)