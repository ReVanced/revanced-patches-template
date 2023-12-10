package app.revanced.patches.tiktok.misc.settings.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

internal object AddSettingsEntryFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.CONST_CLASS,
        Opcode.APUT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("/SettingNewVersionFragment;") &&
                methodDef.name == "onViewCreated"
    }
)