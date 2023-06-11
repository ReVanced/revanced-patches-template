package app.revanced.util.fingerprint

import app.revanced.patcher.fingerprint.method.impl.CustomFingerprint
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.Instruction

object FingerprintUtils {
    inline fun <reified T : Instruction> any(
        opcode: Opcode,
        crossinline predicate: (T) -> Boolean
    ): CustomFingerprint {
        return { methodDef, _ ->
            methodDef.implementation?.instructions?.any {
                if (it.opcode != opcode) return@any false
                if (it !is T) return@any false

                predicate(it)
            } ?: false
        }
    }
}
