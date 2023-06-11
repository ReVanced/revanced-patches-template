package app.revanced.patches.songpal.badge.fingerprints

import app.revanced.patches.songpal.badge.patch.BadgeTabPatch
import app.revanced.util.fingerprint.MethodWithReferenceInstructionFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.immutable.reference.ImmutableMethodReference

object CreateTabsFingerprint : MethodWithReferenceInstructionFingerprint(
    "Ljava/util/List",
    accessFlags = AccessFlags.PRIVATE.value,
    instructionOpcode = Opcode.INVOKE_STATIC,
    methodReference = ImmutableMethodReference(
        BadgeTabPatch.ACTIVITY_TAB_DESCRIPTOR,
        "values",
        emptyList(),
        "[${BadgeTabPatch.ACTIVITY_TAB_DESCRIPTOR}"
    )
)
