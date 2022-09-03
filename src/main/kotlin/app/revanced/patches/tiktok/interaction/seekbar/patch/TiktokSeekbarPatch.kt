package app.revanced.patches.tiktok.interaction.seekbar.patch

import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.tiktok.interaction.seekbar.annotations.SeekbarCompatibility
import app.revanced.patches.tiktok.interaction.seekbar.fingerprints.*
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n
import org.jf.dexlib2.builder.instruction.BuilderInstruction22c

@Patch
@Name("tiktok-seekbar")
@Description("Show progress bar for all video.")
@SeekbarCompatibility
@Version("0.0.1")
@Tags(["interaction"])
class TiktokSeekbarPatch : BytecodePatch(
    listOf(
        AwemeGetVideoControlFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        //Get VideoControl FieldReference
        val videoControl = data.findClass { it.type.endsWith("/VideoControl;") }
            ?: return PatchResultError("Can not find target class")
        val fieldList = videoControl.immutableClass.fields.associateBy { field -> field.name }

        val method = AwemeGetVideoControlFingerprint.result!!.mutableMethod
        val implementation = method.implementation!!
        implementation.addInstructions(
            1, listOf(
                BuilderInstruction11n(Opcode.CONST_4, 1, 1),
                BuilderInstruction22c(Opcode.IPUT, 1, 0, fieldList["showProgressBar"]!!),
                BuilderInstruction22c(Opcode.IPUT, 1, 0, fieldList["draftProgressBar"]!!)
            )
        )
        return PatchResultSuccess()
    }

}