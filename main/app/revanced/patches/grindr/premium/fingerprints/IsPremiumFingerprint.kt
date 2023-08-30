package app.revanced.patches.grindr.premium.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

val _storage = "Lcom/grindrapp/android/storage;"
val session = "Lcom/grindrapp/android/storage/t0;"

object IsPremiumFingerprint : MethodFingerprint(
    "Z",
    customFingerprint = { methodDef, _ ->
        if(methodDef.definingClass == session) {
            println("Found storage class: ${methodDef.definingClass} with method: ${methodDef.name} with return type: ${methodDef.returnType}")
            println(methodDef.accessFlags.toString())
        }

        methodDef.definingClass == session
                && methodDef.name == "c"
                && methodDef.returnType == "Z"
    }
)

/*
        && methodDef.name == "p"
        && methodDef.returnType == "Z"
*/