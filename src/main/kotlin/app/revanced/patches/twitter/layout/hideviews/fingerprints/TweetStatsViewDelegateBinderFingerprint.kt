package app.revanced.patches.twitter.layout.hideviews.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object TweetStatsViewDelegateBinderFingerprint : MethodFingerprint(
    access = AccessFlags.PUBLIC or AccessFlags.FINAL,
    opcodes = listOf(
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.RETURN_OBJECT
    ),
    customFingerprint = { it.definingClass.endsWith("/FocalTweetStatsViewDelegateBinder;") }
)