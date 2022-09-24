package app.revanced.patches.youtube.misc.hdrbrightness.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.misc.hdrbrightness.annotations.HDRBrightnessCompatibility
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.iface.reference.StringReference

@Name("hdr-brightness-fingerprint")
@MatchingMethod(
    "Lyls;", "c"
)
@HDRBrightnessCompatibility
@Version("0.0.1")
object HDRBrightnessFingerprint : MethodFingerprint(
    "V",
    customFingerprint = { methodDef ->
        methodDef.implementation?.instructions?.any {
            ((it.opcode.ordinal == Opcode.IPUT.ordinal
                    ||
            it.opcode.ordinal == Opcode.IGET.ordinal) &&
            ((it as? ReferenceInstruction)?.reference as FieldReference).name.contains("screenBrightness"))
        } == true &&
        methodDef.implementation?.instructions?.any {
            (it.opcode.ordinal == Opcode.CONST_STRING.ordinal &&
            (((it as? ReferenceInstruction)?.reference as StringReference).string == "screen_brightness"
            ||
            ((it as? ReferenceInstruction)?.reference as StringReference).string == "mediaViewambientBrightnessSensor"))
        } == true
    }
)