package app.revanced.patches.songpal.badge.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.songpal.badge.patch.BadgeTabPatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.dexbacked.reference.DexBackedMethodReference
import org.jf.dexlib2.iface.instruction.ReferenceInstruction

// Located @ ub.i0.h#p (9.5.0)
object CreateTabsFingerprint : MethodFingerprint(
    "Ljava/util/List",
    accessFlags = AccessFlags.PRIVATE.value,
    customFingerprint = { methodDef, _ ->
        methodDef.implementation?.instructions?.any { instruction ->
            if (instruction.opcode != Opcode.INVOKE_STATIC) return@any false

            val ref = ((instruction as ReferenceInstruction).reference as DexBackedMethodReference)
            ref.definingClass == BadgeTabPatch.ACTIVITY_TAB_DESCRIPTOR
                    && ref.parameterTypes.isEmpty()
                    && ref.returnType == "[${BadgeTabPatch.ACTIVITY_TAB_DESCRIPTOR}"
        } ?: false
    }
)
