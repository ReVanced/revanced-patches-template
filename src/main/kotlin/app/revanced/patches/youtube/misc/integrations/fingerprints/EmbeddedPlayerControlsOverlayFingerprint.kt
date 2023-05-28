package app.revanced.patches.youtube.misc.integrations.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patches.shared.integrations.patch.AbstractIntegrationsPatch.IntegrationsFingerprint
import org.jf.dexlib2.AccessFlags

// For embedded playback inside Google Play store (and probably other situations as well)
object EmbeddedPlayerControlsOverlayFingerprint : IntegrationsFingerprint(
    accessFlags = AccessFlags.PRIVATE or AccessFlags.CONSTRUCTOR,
    returnType = "V",
    parameters = listOf("L", "L", "L"),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.startsWith("Lcom/google/android/apps/youtube/embeddedplayer/service/ui/overlays/controlsoverlay/remoteloaded/")
    },
    // Context is the first method parameter.
    contextRegisterResolver = { it.implementation!!.registerCount - it.parameters.size }
)