package app.revanced.patches.youtube.misc.integrations.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patches.youtube.misc.integrations.annotations.IntegrationsCompatibility
import app.revanced.patches.youtube.misc.integrations.fingerprints.InitFingerprint
import app.revanced.patches.youtube.misc.integrations.fingerprints.ServiceFingerprint
import app.revanced.patches.youtube.misc.integrations.fingerprints.StandalonePlayerFingerprint
import app.revanced.util.AbstractIntegrationsPatch

@Name("integrations")
@IntegrationsCompatibility
class IntegrationsPatch : AbstractIntegrationsPatch(
    listOf(InitFingerprint, StandalonePlayerFingerprint, ServiceFingerprint),
    "Lapp/revanced/integrations/utils/ReVancedUtils;",
    { m, fp ->
        // parameter which holds the context
        val contextParameter = if (fp == ServiceFingerprint) m.parameters.size else 1
        // register which holds the context
        m.implementation!!.registerCount - contextParameter
    }
)