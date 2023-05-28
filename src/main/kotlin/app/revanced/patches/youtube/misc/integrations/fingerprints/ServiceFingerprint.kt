package app.revanced.patches.youtube.misc.integrations.fingerprints

import app.revanced.patches.shared.integrations.patch.AbstractIntegrationsPatch.IntegrationsFingerprint

// Edit: what situation is this used for?
object ServiceFingerprint : IntegrationsFingerprint(
    customFingerprint = { methodDef, _ -> methodDef.definingClass.endsWith("ApiPlayerService;") && methodDef.name == "<init>" },
    contextRegisterResolver = { it.implementation!!.registerCount - it.parameters.size }
)