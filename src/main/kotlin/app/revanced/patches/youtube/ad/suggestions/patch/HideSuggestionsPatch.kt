package app.revanced.patches.youtube.ad.suggestions.patch

import OldQualityFingerprint
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstructions
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.youtube.ad.suggestions.annotations.HideSuggestionsCompatibility
import app.revanced.patches.youtube.ad.suggestions.fingerprints.HideSuggestionsFingerprint
import app.revanced.patches.youtube.ad.suggestions.fingerprints.HideSuggestionsParentFingerprint
import app.revanced.patches.youtube.layout.oldqualitylayout.annotations.OldQualityLayoutCompatibility
import app.revanced.patches.youtube.layout.oldqualitylayout.fingerprints.OldQualityParentFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction21t

@Patch
@Dependencies(dependencies = [IntegrationsPatch::class])
@Name("hide-infocards")
@Description("Hides infocards in videos.")
@HideSuggestionsCompatibility
@Version("0.0.1")
class HideSuggestionsPatch : BytecodePatch(
    listOf(
        HideSuggestionsParentFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val parentResult = HideSuggestionsParentFingerprint.result
            ?: return PatchResultError("Parent fingerprint not resolved!")


        HideSuggestionsFingerprint.resolve(data, parentResult.classDef)
        val result = HideSuggestionsFingerprint.result
            ?: return PatchResultError("Required parent method could not be found.")

        val method = result.mutableMethod
        val implementation = method.implementation
            ?: return PatchResultError("Implementation not found.")

        method.removeInstructions(0, implementation.instructions.size - 1)
        method.addInstructions(
            0, """
                const/4 p1, 0x1
                invoke-static {p1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;
                move-result-object p1
                return-object p1
        """
        )

        return PatchResultSuccess()
    }
}