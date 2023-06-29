package app.revanced.patches.youtube.layout.hide.albumcards.bytecode.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.hide.albumcards.annotations.AlbumCardsCompatibility
import app.revanced.patches.youtube.layout.hide.albumcards.bytecode.fingerprints.AlbumCardsFingerprint
import app.revanced.patches.youtube.layout.hide.albumcards.resource.patch.AlbumCardsResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class, AlbumCardsResourcePatch::class])
@Name("hide-album-cards")
@Description("Hides the album cards below the artist description.")
@AlbumCardsCompatibility
@Version("0.0.1")
class AlbumCardsPatch : BytecodePatch(
    listOf(
        AlbumCardsFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        AlbumCardsFingerprint.result?.let {
            it.mutableMethod.apply {
                val checkCastAnchorIndex = it.scanResult.patternScanResult!!.endIndex
                val insertIndex = checkCastAnchorIndex + 1

                val albumCardViewRegister = getInstruction<OneRegisterInstruction>(checkCastAnchorIndex).registerA

                addInstruction(
                    insertIndex,
                    "invoke-static {v$albumCardViewRegister}, " +
                            "Lapp/revanced/integrations/patches/HideAlbumCardsPatch;" +
                            "->" +
                            "hideAlbumCard(Landroid/view/View;)V"
                )
            }
        } ?: return AlbumCardsFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}
