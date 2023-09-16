package app.revanced.patches.strava.upselling.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object GetModulesFingerprint : MethodFingerprint(
    opcodes = listOf(Opcode.IGET_OBJECT),
    customFingerprint = { methodDef, classDef ->
        classDef.type.endsWith("/GenericLayoutEntry;") && methodDef.name == "getModules"
    }
)
