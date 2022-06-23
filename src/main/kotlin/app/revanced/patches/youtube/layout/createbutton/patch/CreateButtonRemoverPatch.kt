package app.revanced.patches.youtube.layout.createbutton.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.implementation.BytecodeData
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.implementation.BytecodePatch
import app.revanced.patcher.patch.implementation.misc.PatchResult
import app.revanced.patcher.patch.implementation.misc.PatchResultError
import app.revanced.patcher.patch.implementation.misc.PatchResultSuccess
import app.revanced.patcher.util.smali.toInstruction
import app.revanced.patches.youtube.layout.createbutton.annotations.CreateButtonCompatibility
import app.revanced.patches.youtube.layout.createbutton.signatures.CreateButtonSignature
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.mapping.patch.ResourceIdMappingProviderResourcePatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.instruction.formats.Instruction31i
import org.jf.dexlib2.iface.instruction.formats.Instruction11x
import org.jf.dexlib2.iface.reference.MethodReference

@Patch
@Dependencies(dependencies = [IntegrationsPatch::class, ResourceIdMappingProviderResourcePatch::class])
@Name("disable-create-button")
@Description("Disable the create button.")
@CreateButtonCompatibility
@Version("0.0.1")
class CreateButtonRemoverPatch : BytecodePatch(
    listOf(
        CreateButtonSignature
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val result = CreateButtonSignature.result!!

        // Get the required register which holds the view object we need to pass to the method hideCreateButton
        val implementation = result.method.implementation!!

        val imageOnlyLayout = ResourceIdMappingProviderResourcePatch.resourceMappings.get("image_only_tab")?.toInt()
        val imageOnlyLayoutConstIndex =  implementation.instructions.indexOfFirst { (it as? Instruction31i)?.narrowLiteral == imageOnlyLayout }

        val (instructionIndex, instruction) = implementation.instructions.drop(imageOnlyLayoutConstIndex).withIndex()
            .first { (((it.value as? Instruction35c)?.reference) as? MethodReference)?.definingClass?.contains("PivotBar") ?: false }

        if (instruction.opcode != Opcode.INVOKE_VIRTUAL) return PatchResultError("Could not find the correct instruction")

        val moveResultIndex = imageOnlyLayoutConstIndex + instructionIndex + 1
        val moveResultInstruction = implementation.instructions[moveResultIndex] as Instruction11x

        // Hide the button view via proxy by passing it to the hideCreateButton method
        implementation.addInstruction(
            moveResultIndex + 1,
            "invoke-static { v${moveResultInstruction.registerA} }, Lfi/razerman/youtube/XAdRemover;->hideCreateButton(Landroid/view/View;)V".toInstruction()
        )

        return PatchResultSuccess()
    }
}
