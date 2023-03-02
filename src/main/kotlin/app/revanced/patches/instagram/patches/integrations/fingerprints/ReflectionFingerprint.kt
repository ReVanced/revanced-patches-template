package app.revanced.patches.instagram.patches.integrations.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object ReflectionFingerprint : MethodFingerprint(
    strings = listOf("kotlin.internal.jdk8.JDK8PlatformImplementations"),
    opcodes = listOf(
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.SPUT_OBJECT
    ),
)
