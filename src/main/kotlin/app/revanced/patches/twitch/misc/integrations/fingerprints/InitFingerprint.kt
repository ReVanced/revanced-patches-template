package app.revanced.patches.twitch.misc.integrations.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patches.twitch.misc.integrations.annotations.TwitchIntegrationsCompatibility
import app.revanced.shared.patches.AbstractIntegrationsPatch.IntegrationsFingerprint

@Name("init-fingerprint")
@TwitchIntegrationsCompatibility
@Version("0.0.1")
object InitFingerprint : IntegrationsFingerprint(
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/TwitchApplication;") &&
                methodDef.name == "onCreate"
    }
)