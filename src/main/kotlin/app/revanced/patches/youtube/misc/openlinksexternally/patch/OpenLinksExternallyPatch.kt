package app.revanced.patches.youtube.misc.openlinksexternally.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.openlinksexternally.annotations.OpenLinksExternallyCompatibility
import app.revanced.patches.youtube.misc.openlinksexternally.fingerprints.*
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import org.jf.dexlib2.iface.instruction.formats.Instruction21c

@Patch
@Name("open-links-externally")
@Description("Use an external browser to open the links.")
@OpenLinksExternallyCompatibility
@Version("0.0.1")
class OpenLinksExternallyPatch : BytecodePatch(
    listOf(
        OpenLinksExternallyPrimaryFingerprint,
        OpenLinksExternallySecondaryFingerprint,
        OpenLinksExternallyTertiaryFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_enable_external_browser",
                StringResource("revanced_enable_external_browser_title", "Open links in external browser"),
                true,
                StringResource("revanced_enable_external_browser_summary_on", "Opening links externally"),
                StringResource("revanced_enable_external_browser_summary_off", "Opening links in YouTube Revanced")
            )
        )

        arrayOf(
            OpenLinksExternallyPrimaryFingerprint,
            OpenLinksExternallySecondaryFingerprint,
            OpenLinksExternallyTertiaryFingerprint
        ).forEach {
            val result = it.result ?: return it.toErrorResult()
            val endIndex = result.scanResult.patternScanResult!!.endIndex
            with(result.mutableMethod) {
                val register = (implementation!!.instructions[endIndex] as Instruction21c).registerA
                addInstructions(
                    endIndex + 1, """
                        invoke-static {v$register}, Lapp/revanced/integrations/patches/OpenLinksExternallyPatch;->enableExternalBrowser(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$register
                    """
                )
            }
        }

        return PatchResultSuccess()
    }
}