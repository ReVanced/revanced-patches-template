package app.revanced.patches.youtube.misc.parseuriredirect.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.parseuriredirect.annotations.ParseUriRedirectCompatibility
import app.revanced.patches.youtube.misc.parseuriredirect.fingerprints.ParseUriRedirectFirstFingerprint
import app.revanced.patches.youtube.misc.parseuriredirect.fingerprints.ParseUriRedirectSecondFingerprint
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.instruction.formats.Instruction11x

@Patch
@DependsOn([IntegrationsPatch::class])
@Name("uri-redirect")
@Description("Follow direct links, bypassing youtube.com/redirect.")
@ParseUriRedirectCompatibility
@Version("0.0.1")
class ParseUriRedirectPatch : BytecodePatch(
    listOf(
        ParseUriRedirectFirstFingerprint, ParseUriRedirectSecondFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_uri_redirect",
                StringResource("revanced_uri_redirect_title", "Open links directly"),
                true,
                StringResource("revanced_uri_redirect_summary_on", "Enabled"),
                StringResource("revanced_uri_redirect_summary_off" , "Follow redirect policy of YouTube")
            )
        )

        val parseUriRedirectFirstFingerprintResult = ParseUriRedirectFirstFingerprint.result!!
        val parseUriFirstPatternScanStartIndex = parseUriRedirectFirstFingerprintResult.scanResult.patternScanResult!!.startIndex
        val firstTargetRegister =
            (parseUriRedirectFirstFingerprintResult.method.implementation!!.instructions.elementAt(parseUriFirstPatternScanStartIndex + 1) as Instruction35c).registerC

        parseUriRedirectFirstFingerprintResult.mutableMethod.addInstructions(
            parseUriFirstPatternScanStartIndex + 1, """
                    invoke-static {v$firstTargetRegister}, Lapp/revanced/integrations/patches/UriRedirectPatch;->parseRedirectUri(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v$firstTargetRegister
            """
        )

        val parseUriRedirectSecondFingerprintResult = ParseUriRedirectSecondFingerprint.result!!
        val parseUriSecondPatternScanStartIndex = parseUriRedirectSecondFingerprintResult.scanResult.patternScanResult!!.startIndex
        val secondTargetRegister =
            (parseUriRedirectSecondFingerprintResult.method.implementation!!.instructions.elementAt(parseUriSecondPatternScanStartIndex + 1) as Instruction11x).registerA

        parseUriRedirectSecondFingerprintResult.mutableMethod.addInstructions(
            parseUriSecondPatternScanStartIndex + 2, """
                    invoke-static {v$secondTargetRegister}, Lapp/revanced/integrations/patches/UriRedirectPatch;->parseRedirectUri(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v$secondTargetRegister
            """
        )

        return PatchResultSuccess()
    }
}