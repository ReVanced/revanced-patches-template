package app.revanced.patches.anytracker.misc.premium.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object IsPurchasedFlowFingerprint : MethodFingerprint(
    access = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    customFingerprint = { methodDef ->
        methodDef.definingClass.contains("BillingDataSource\$") &&
                methodDef.parameterTypes.size == 1 &&
                methodDef.parameterTypes.first().endsWith("Flow;")
    },
    opcodes = listOf(
        Opcode.IPUT_OBJECT,
        Opcode.INVOKE_DIRECT,
        Opcode.RETURN_VOID
    )
)