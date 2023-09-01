package app.revanced.patches.grindr.unlimited.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

//a

@FuzzyPatternScanMethod(2)
object HasFeatureFingerprint : MethodFingerprint(
    "Z",
    parameters = listOf("Lcom/grindrapp/android/model/Feature;"),
    customFingerprint = { methodDef, _ ->
        methodDef.name.contains("a")
    }
)