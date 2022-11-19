package app.revanced.patches.twitch.misc.integrations.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.twitch.misc.integrations.annotations.TwitchIntegrationsCompatibility

@Name("init-fingerprint")
@TwitchIntegrationsCompatibility
@Version("0.0.1")
object InitFingerprint : MethodFingerprint(
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/TwitchApplication;") &&
                methodDef.name == "onCreate"
    }
)