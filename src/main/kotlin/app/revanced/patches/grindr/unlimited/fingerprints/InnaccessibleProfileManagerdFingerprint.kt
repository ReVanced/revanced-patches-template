package app.revanced.patches.grindr.unlimited.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

//d

object InnaccessibleProfileManagerdFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.name == "d" && methodDef.definingClass.contains("InaccessibleProfileManager")
    }
)