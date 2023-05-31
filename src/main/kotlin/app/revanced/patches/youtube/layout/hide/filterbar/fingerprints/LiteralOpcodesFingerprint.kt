package app.revanced.patches.youtube.layout.hide.filterbar.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

abstract class LiteralOpcodesFingerprint(returnType: String?, accessFlags: Int?, opcodes: List<Opcode>, literal: Long) : MethodFingerprint(
    returnType = returnType,
    accessFlags = accessFlags,
    opcodes = opcodes,
    customFingerprint = { methodDef, _ ->
        methodDef.implementation?.instructions?.any { instruction ->
            if (instruction.opcode != Opcode.CONST) return@any false

            val wideLiteral = (instruction as WideLiteralInstruction).wideLiteral

            literal == wideLiteral
        } ?: false
    }
)