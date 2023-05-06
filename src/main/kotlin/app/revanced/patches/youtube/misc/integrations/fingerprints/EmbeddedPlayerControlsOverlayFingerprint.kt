package app.revanced.patches.youtube.misc.integrations.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patches.shared.integrations.patch.AbstractIntegrationsPatch.IntegrationsFingerprint
import org.jf.dexlib2.AccessFlags

object EmbeddedPlayerControlsOverlayFingerprint : IntegrationsFingerprint(
    access = AccessFlags.PRIVATE or AccessFlags.CONSTRUCTOR,
    returnType = "V",
    parameters = listOf("Landroid/content/Context;", "Lcom/google/android/apps/youtube/embeddedplayer/service/layoutpolicy/remoteloaded/", "L"),
    customFingerprint = { methodDef ->
        methodDef.definingClass.startsWith("Lcom/google/android/apps/youtube/embeddedplayer/service/ui/overlays/controlsoverlay/remoteloaded/")
    },
    contextRegisterResolver = { it.implementation!!.registerCount - it.parameters.size }
)