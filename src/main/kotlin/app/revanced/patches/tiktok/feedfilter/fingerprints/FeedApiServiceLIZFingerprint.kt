package app.revanced.patches.tiktok.feedfilter.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object FeedApiServiceLIZFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.RETURN_OBJECT,
        Opcode.MOVE_EXCEPTION
    ),
    customFingerprint = { it.definingClass.endsWith("/FeedApiService;") }
)