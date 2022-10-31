package app.revanced.patches.youtube.layout.hidecrowdfundingbox.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.hidecrowdfundingbox.resource.patch.CrowdfundingBoxResourcePatch
import app.revanced.patches.youtube.layout.hidecrowdfundingbox.annotations.CrowdfundingBoxCompatibility
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Name("crowdfunding-box-view-fingerprint")
@CrowdfundingBoxCompatibility
@Version("0.0.1")
object CrowdfundingBoxFingerprint : MethodFingerprint(
    customFingerprint = { methodDef ->
        methodDef.implementation?.instructions?.any { instruction ->
            (instruction as? WideLiteralInstruction)?.wideLiteral == CrowdfundingBoxResourcePatch.crowdfundingBoxId
        } == true
    }
)