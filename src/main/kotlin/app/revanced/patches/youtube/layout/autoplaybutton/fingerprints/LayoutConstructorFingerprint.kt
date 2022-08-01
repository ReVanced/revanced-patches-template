package app.revanced.patches.youtube.layout.autoplaybutton.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.autoplaybutton.annotations.AutoplayButtonCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("layout-constructor-fingerprint")
@MatchingMethod(
    "LYouTubeControlsOverlay;", "F"
)
@FuzzyPatternScanMethod(2)
@AutoplayButtonCompatibility
@Version("0.0.1")
object LayoutConstructorFingerprint : MethodFingerprint(
    "V",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    null,
    listOf(
        Opcode.CONST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.INVOKE_INTERFACE,
        Opcode.IGET_OBJECT,
        Opcode.CONST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,
        Opcode.CONST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.CONST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.IPUT_OBJECT,
        Opcode.NEW_INSTANCE,
        Opcode.CONST_4,
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.CONST_STRING,

        ),
    listOf("1.0x")
)