package app.revanced.patches.tumblr.timelinefilter.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

// This fingerprints the Integration TimelineFilterPatch.filterTimeline method.
// The opcode fingerprint is searching for
//   if ("BLOCKED_OBJECT_DUMMY".equals(elementType)) iterator.remove();
object TimelineFilterIntegrationFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ -> methodDef.definingClass.endsWith("/TimelineFilterPatch;") },
    strings = listOf("BLOCKED_OBJECT_DUMMY"),
    opcodes = listOf(
        Opcode.CONST_STRING, // "BLOCKED_OBJECT_DUMMY"
        Opcode.INVOKE_VIRTUAL, // ^.equals(elementType)
        Opcode.MOVE_RESULT,
        Opcode.IF_EQZ, // Jump to start of loop if not equals
        Opcode.INVOKE_INTERFACE // iterator.remove()
    )
)