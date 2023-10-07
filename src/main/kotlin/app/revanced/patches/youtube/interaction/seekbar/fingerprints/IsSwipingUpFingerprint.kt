package app.revanced.patches.youtube.interaction.seekbar.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object IsSwipingUpFingerprint : MethodFingerprint(
    parameters = listOf("Landroid/view/MotionEvent;", "J"),
    opcodes = listOf(Opcode.SGET_OBJECT)
)