package app.revanced.patches.youtube.misc.links.open.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsResourcePatch
import app.revanced.util.integrations.Constants.MISC_PATH
import org.jf.dexlib2.iface.instruction.formats.Instruction35c

import app.revanced.patches.youtube.misc.links.open.annotations.OpenLinksDirectlyCompatibility

import app.revanced.patches.youtube.misc.links.open.fingerprints.OpenLinksDirectlyFingerprintSecondary
import app.revanced.patches.youtube.misc.links.open.fingerprints.OpenLinksDirectlyFingerprintPrimary
import app.revanced.patches.youtube.misc.links.open.annotations.OpenLinksDirectlyCompatibility

@Patch
@Name("enable-open-links-directly")
@Description("Skips over redirection URLs to external links.")
@DependsOn([SettingsResourcePatch::class])
@YouTubeCompatibility
@Version("0.0.1")
class OpenLinksDirectlyPatch : BytecodePatch(
    listOf(
        OpenLinksDirectlyFingerprintPrimary,
        OpenLinksDirectlyFingerprintSecondary
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        arrayOf(
            OpenLinksDirectlyFingerprintPrimary,
            OpenLinksDirectlyFingerprintSecondary
        ).forEach {
            val result = it.result?: return it.toErrorResult()
            val insertIndex = result.scanResult.patternScanResult!!.startIndex
            result.mutableMethod.apply {
                val register = instruction<Instruction35c>(insertIndex).registerC
                replaceInstruction(
                    insertIndex,
                        "invoke-static {v$register}, $MISC_PATH/OpenLinksDirectlyPatch;->enableBypassRedirect(Ljava/lang/String;)Landroid/net/Uri;"
                )
            }
        }

        /**
         * Add settings
         */
        SettingsResourcePatch.addPreference(
            arrayOf(
                "SETTINGS: ENABLE_OPEN_LINKS_DIRECTLY"
            )
        )

        SettingsResourcePatch.updatePatchStatus("enable-open-links-directly")

        return PatchResultSuccess()
    }
}
