package app.revanced.patches.music.layout.compactheader.patch

import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.music.layout.compactheader.annotations.CompactHeaderCompatibility
import app.revanced.patches.music.layout.compactheader.fingerprints.CompactHeaderConstructorFingerprint
import org.jf.dexlib2.builder.instruction.BuilderInstruction11x

@Patch(false)
@Name("compact-header")
@Description("Hides the music category bar at the top of the homepage.")
@CompactHeaderCompatibility
@Version("0.0.1")
@Tags(["layout"])
class CompactHeaderPatch : BytecodePatch(
    listOf(
        CompactHeaderConstructorFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val result = CompactHeaderConstructorFingerprint.result!!
        val method = result.mutableMethod

        val insertIndex = result.patternScanResult!!.endIndex
        val register = (method.implementation!!.instructions[insertIndex - 1] as BuilderInstruction11x).registerA
        method.addInstructions(
            insertIndex, """
                const/16 v2, 0x8
                invoke-virtual {v${register}, v2}, Landroid/view/View;->setVisibility(I)V
            """
        )

        return PatchResultSuccess()
    }
}
