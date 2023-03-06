package app.revanced.patches.tiktok.misc.settings.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object AboutPageFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.CONST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CONST_STRING
    ),
    customFingerprint = {
        it.definingClass == "Lcom/ss/android/ugc/aweme/setting/page/AboutPage;" &&
                it.name == "onViewCreated"
    }
)