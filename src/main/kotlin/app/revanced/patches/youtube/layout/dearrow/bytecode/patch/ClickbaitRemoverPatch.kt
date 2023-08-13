package app.revanced.patches.youtube.layout.dearrow.bytecode.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.Preference
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.youtube.layout.dearrow.annotations.DeArrowCompatibility
import app.revanced.patches.youtube.layout.dearrow.bytecode.fingerprints.CronetURLRequestCallbackOnFailureFingerprint
import app.revanced.patches.youtube.layout.dearrow.bytecode.fingerprints.CronetURLRequestCallbackOnResponseStartedFingerprint
import app.revanced.patches.youtube.layout.dearrow.bytecode.fingerprints.CronetURLRequestCallbackOnSucceededFingerprint
import app.revanced.patches.youtube.layout.dearrow.bytecode.fingerprints.MessageDigestImageUrlFingerprint
import app.revanced.patches.youtube.layout.dearrow.bytecode.fingerprints.MessageDigestImageUrlParentFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("Clickbait remover")
@DeArrowCompatibility
@Description("Uses DeArrow API to alters video thumbnails to better represent the video content")
class ClickbaitRemoverPatch : BytecodePatch(
    listOf(
        MessageDigestImageUrlParentFingerprint,
        CronetURLRequestCallbackOnResponseStartedFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        SettingsPatch.addPreference(
            Preference(
                StringResource("revanced_clickbait_settings_title", "Clickbait remover"),
                StringResource("revanced_clickbait_settings_summary", "Clickbait remover related settings"),
                SettingsPatch.createReVancedSettingsIntent("clickbait_settings")
            )
        )

        MessageDigestImageUrlParentFingerprint.result ?: return MessageDigestImageUrlParentFingerprint.toErrorResult()
        MessageDigestImageUrlFingerprint.resolve(context, MessageDigestImageUrlParentFingerprint.result!!.classDef)
        MessageDigestImageUrlFingerprint.result?.apply {
            mutableMethod.addInstructions(
                0,
                """
                    invoke-static {p1}, $INTEGRATIONS_CLASS_DESCRIPTOR->overrideImageURL(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object p1
                """
            )
        } ?: return MessageDigestImageUrlFingerprint.toErrorResult()

        CronetURLRequestCallbackOnResponseStartedFingerprint.result ?: return CronetURLRequestCallbackOnResponseStartedFingerprint.toErrorResult()
        CronetURLRequestCallbackOnSucceededFingerprint.resolve(
            context,
            CronetURLRequestCallbackOnResponseStartedFingerprint.result!!.classDef
        )
        CronetURLRequestCallbackOnSucceededFingerprint.result?.apply {
            mutableMethod.addInstructions(
                0,
                """
                    invoke-static {p1, p2}, $INTEGRATIONS_CLASS_DESCRIPTOR->handleCronetSucceeded(Ljava/lang/Object;Lorg/chromium/net/UrlResponseInfo;)V
                """
            )
        } ?: return CronetURLRequestCallbackOnSucceededFingerprint.toErrorResult()

        CronetURLRequestCallbackOnFailureFingerprint.resolve(
            context,
            CronetURLRequestCallbackOnResponseStartedFingerprint.result!!.classDef
        )
        CronetURLRequestCallbackOnFailureFingerprint.result?.apply {
            mutableMethod.addInstructions(
                0,
                """
                    invoke-static {p1, p2, p3}, $INTEGRATIONS_CLASS_DESCRIPTOR->handleCronetFailure(Ljava/lang/Object;Lorg/chromium/net/UrlResponseInfo;Ljava/io/IOException;)V
                """
            )
        } ?: return CronetURLRequestCallbackOnFailureFingerprint.toErrorResult()


        return PatchResultSuccess()
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/ClickbaitRemoverPatch;"
    }
}
