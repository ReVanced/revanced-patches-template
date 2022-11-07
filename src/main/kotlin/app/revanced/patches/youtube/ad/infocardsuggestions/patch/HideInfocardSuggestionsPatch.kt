package app.revanced.patches.youtube.ad.infocardsuggestions.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.ad.infocardsuggestions.annotations.HideInfocardSuggestionsCompatibility
import app.revanced.patches.youtube.ad.infocardsuggestions.fingerprints.HideInfocardSuggestionsFingerprint
import app.revanced.patches.youtube.ad.infocardsuggestions.fingerprints.HideInfocardSuggestionsHeaderFingerprint
import app.revanced.patches.youtube.ad.infocardsuggestions.fingerprints.HideInfocardSuggestionsParentFingerprint
import app.revanced.patches.youtube.ad.infocardsuggestions.resource.patch.HideInfocardSuggestionsResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c

@Patch
@DependsOn([IntegrationsPatch::class, HideInfocardSuggestionsResourcePatch::class])
@Name("hide-infocard-suggestions")
@Description("Hides infocards in videos.")
@HideInfocardSuggestionsCompatibility
@Version("0.0.1")
class HideInfocardSuggestionsPatch : BytecodePatch(
    listOf(
        HideInfocardSuggestionsParentFingerprint,
        HideInfocardSuggestionsHeaderFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val parentResult = HideInfocardSuggestionsParentFingerprint.result
            ?: return PatchResultError("Parent fingerprint not resolved!")


        HideInfocardSuggestionsFingerprint.resolve(context, parentResult.classDef)
        val result = HideInfocardSuggestionsFingerprint.result
            ?: return PatchResultError("Required parent method could not be found.")

        val method = result.mutableMethod
        val implementation = method.implementation
            ?: return PatchResultError("Implementation not found.")

        val index = implementation.instructions.indexOfFirst { ((it as? BuilderInstruction35c)?.reference.toString() == "Landroid/view/View;->setVisibility(I)V") }

        method.replaceInstruction(index, """
            invoke-static {p1}, Lapp/revanced/integrations/patches/HideInfoCardSuggestionsPatch;->hideInfoCardSuggestions(Landroid/view/View;)V
        """)

        // hide the header creation to prevent the info card to pop up for a second
        val hideInfocardsHeaderResult = HideInfocardSuggestionsHeaderFingerprint.result!!
        val hideInfocardsHeaderMethod = hideInfocardsHeaderResult.mutableMethod

        val invokeInterfaceIndex = hideInfocardsHeaderResult.scanResult.patternScanResult!!.endIndex
        val toggleRegister = hideInfocardsHeaderMethod.implementation!!.registerCount - 1

        hideInfocardsHeaderMethod.addInstructions(
            invokeInterfaceIndex, """
                invoke-static {}, Lapp/revanced/integrations/patches/HideInfoCardSuggestionsPatch;->hideInfoCardHeaderSuggestions()Z
                move-result v$toggleRegister
                if-eqz v$toggleRegister, :hide_info_cards_header
            """, listOf(ExternalLabel("hide_info_cards_header", hideInfocardsHeaderMethod.instruction(invokeInterfaceIndex + 1)))
        )

        return PatchResultSuccess()
    }

}