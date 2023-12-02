package app.revanced.patches.reddit.ad.general.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

internal object NewAdPostFingerprint : MethodFingerprint(
    opcodes = listOf(Opcode.INVOKE_VIRTUAL),
    strings = listOf("chain", "feedElement"),
    customFingerprint = { _, classDef -> classDef.sourceFile == "AdElementConverter.kt" },
)