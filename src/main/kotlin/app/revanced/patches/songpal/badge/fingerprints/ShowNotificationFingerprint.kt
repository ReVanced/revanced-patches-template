package app.revanced.patches.songpal.badge.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.dexbacked.reference.DexBackedMethodReference
import org.jf.dexlib2.iface.instruction.ReferenceInstruction

// Located @ com.sony.songpal.mdr.vim.activity.MdrRemoteBaseActivity.e#run (9.5.0)
object ShowNotificationFingerprint : MethodFingerprint(
    "V",
    accessFlags = AccessFlags.PUBLIC.value,
    customFingerprint = { methodDef, _ ->
        methodDef.implementation?.instructions?.any { instruction ->
            if (instruction.opcode != Opcode.INVOKE_VIRTUAL) return@any false
            if (instruction !is ReferenceInstruction) return@any false
            if (instruction.reference !is DexBackedMethodReference) return@any false

            val ref = (instruction.reference as DexBackedMethodReference)
            ref.definingClass == "Lcom/google/android/material/bottomnavigation/BottomNavigationView;"
                    && ref.parameterTypes.size == 1 && ref.parameterTypes[0] == "I"
                    && ref.returnType == "Lcom/google/android/material/badge/BadgeDrawable;"
        } ?: false
    }
)
