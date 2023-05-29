package app.revanced.patches.youtube.misc.integrations.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patches.shared.integrations.patch.AbstractIntegrationsPatch.IntegrationsFingerprint
import org.jf.dexlib2.AccessFlags

/**
 * For embedded playback.  Likely covers Google Play store and other Google products.
 */
object RemoteEmbedFragmentFingerprint : IntegrationsFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    returnType = "V",
    parameters = listOf("Landroid/content/Context;", "L", "L"),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass == "Lcom/google/android/apps/youtube/embeddedplayer/service/jar/client/RemoteEmbedFragment;"
    },
    // Integrations context is the first method parameter.
    contextRegisterResolver = { it.implementation!!.registerCount - it.parameters.size }
)