package app.revanced.patches.youtube.misc.integrations.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patches.shared.integrations.patch.AbstractIntegrationsPatch.IntegrationsFingerprint
import org.jf.dexlib2.AccessFlags

object EmbeddedPlayerFingerprint : IntegrationsFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    returnType = "L",
    parameters = listOf("L", "L", "Landroid/content/Context;"),
    strings = listOf("android.hardware.type.television"), // String is also found in other classes
    contextRegisterResolver = { it.implementation!!.registerCount - it.parameters.size + 2 }
)