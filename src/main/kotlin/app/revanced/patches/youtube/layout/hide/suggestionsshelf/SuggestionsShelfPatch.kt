package app.revanced.patches.youtube.layout.hide.suggestionsshelf

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.layout.hide.suggestionsshelf.fingerprints.BreakingNewsFingerprint
import app.revanced.patches.youtube.layout.utils.navbarindexhook.NavBarIndexHookPatch
import app.revanced.patches.youtube.misc.litho.filter.LithoFilterPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Hide Suggestions shelf",
    description = "Hides suggestions shelf on the homepage tab.",
    dependencies = [
        SettingsPatch::class,
        LithoFilterPatch::class,
        NavBarIndexHookPatch::class,
        SuggestionsShelfResourcePatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube", [
                "18.29.38",
                "18.32.39",
                "18.37.36",
                "18.38.44"
            ]
        )
    ]
)
@Suppress("unused")
object SuggestionsShelfPatch : BytecodePatch(
    setOf(BreakingNewsFingerprint)
) {
    private const val FILTER_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/components/SuggestionsShelfFilter;"

    override fun execute(context: BytecodeContext) {
        /**
         * Old breaking news shelf UI patch.  Used only for tablets.
         */
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

}
