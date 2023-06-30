package app.revanced.patches.youtube.misc.integrations.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patches.shared.integrations.patch.AbstractIntegrationsPatch.IntegrationsFingerprint
import org.jf.dexlib2.AccessFlags

/**
 * Old API activity to embed YouTube into 3rd party Android apps.
 *
 * In 2023 supported was ended and is no longer available,
 * but this may still be used by older apps:
 * https://developers.google.com/youtube/android/player
 */
object StandalonePlayerActivityFingerprint : IntegrationsFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "V",
    parameters = listOf("L"),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass == "Lcom/google/android/youtube/api/StandalonePlayerActivity;"
                && methodDef.name == "onCreate"
    },
    // Integrations context is the Activity itself.
)