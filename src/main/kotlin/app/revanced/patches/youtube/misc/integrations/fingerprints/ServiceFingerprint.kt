package app.revanced.patches.youtube.misc.integrations.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.misc.integrations.annotations.IntegrationsCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("service-fingerprint")
@MatchingMethod(
    "YoutubeService", "<init>"
)
@IntegrationsCompatibility
@Version("0.0.1")
object ServiceFingerprint : MethodFingerprint(
    "V", AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR, null, listOf(
        Opcode.INVOKE_DIRECT,
        Opcode.RETURN_VOID
    ),
)