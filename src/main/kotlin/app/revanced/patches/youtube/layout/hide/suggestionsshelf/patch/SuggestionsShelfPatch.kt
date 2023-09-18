package app.revanced.patches.youtube.layout.hide.suggestionsshelf.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.suggestionsshelf.annotations.SuggestionsShelfCompatibility
import app.revanced.patches.youtube.layout.hide.suggestionsshelf.fingerprints.BreakingNewsFingerprint
import app.revanced.patches.youtube.layout.hide.suggestionsshelf.resource.patch.BreakingNewsResourcePatch
import app.revanced.patches.youtube.layout.utils.navbarindexhook.patch.NavBarIndexHookPatch
import app.revanced.patches.youtube.misc.litho.filter.patch.LithoFilterPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([LithoFilterPatch::class, NavBarIndexHookPatch::class, BreakingNewsResourcePatch::class])
@Name("Hide Suggestions shelf")
@Description("Hides suggestions shelf on the homepage tab.")
@SuggestionsShelfCompatibility
class SuggestionsShelfPatch : BytecodePatch(
    listOf(BreakingNewsFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_suggestions_shelf",
                StringResource("revanced_hide_suggestions_shelf_title", "Hide Suggestions shelves"),
                StringResource("revanced_hide_suggestions_shelf_on", "Suggestions shelves are hidden"),
                StringResource("revanced_hide_suggestions_shelf_off", "Suggestions shelves are shown")
            )
        )

        BreakingNewsFingerprint.result?.let {
            val insertIndex = it.scanResult.patternScanResult!!.endIndex - 1
            val moveResultIndex = insertIndex - 1

            it.mutableMethod.apply {
                val breakingNewsViewRegister = getInstruction<OneRegisterInstruction>(moveResultIndex).registerA

                addInstruction(
                    insertIndex,
                    """
                        invoke-static {v$breakingNewsViewRegister}, $FILTER_CLASS_DESCRIPTOR->hideBreakingNews(Landroid/view/View;)V
                    """
                )
            }

        } ?: throw BreakingNewsFingerprint.exception

        LithoFilterPatch.addFilter(FILTER_CLASS_DESCRIPTOR)
    }

    private companion object {
        const val FILTER_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/components/SuggestionsShelfFilter;"
    }
}
