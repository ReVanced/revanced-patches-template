package app.revanced.patches.truecaller.misc.subscription.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.truecaller.misc.subscription.annotations.SubscriptionUnlockCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("subscription-unlock")
@SubscriptionUnlockCompatibility
@Version("0.0.1")
object SubscriptionUnlockFingerprint2 : MethodFingerprint(
    access = AccessFlags.PUBLIC or AccessFlags.FINAL,
    opcodes =  listOf(
        Opcode.RETURN_OBJECT,
        Opcode.IGET_BOOLEAN,
        Opcode.IF_EQZ,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT,
        Opcode.GOTO,
        Opcode.MOVE,
        Opcode.IF_NEZ,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.CONST_4,
    ),
    strings = listOf("object : TypeToken<T>() {}.type", "this.fromJson(json, typeToken<T>())", ",")
)