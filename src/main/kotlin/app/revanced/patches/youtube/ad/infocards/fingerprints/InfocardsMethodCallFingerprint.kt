package app.revanced.patches.youtube.ad.infocards.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.ad.infocards.annotations.HideInfocardsCompatibility
import app.revanced.patches.youtube.ad.infocards.resource.patch.HideInfocardsResourcePatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Name("infocards-method-call-fingerprint")
@HideInfocardsCompatibility
@Version("0.0.1")
object InfocardsMethodCallFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_INTERFACE,
    ),
    customFingerprint = { methodDef ->
        methodDef.implementation?.instructions?.any { instruction ->
            (instruction as? WideLiteralInstruction)?.wideLiteral == HideInfocardsResourcePatch.drawerResourceId
        } == true
    }
)