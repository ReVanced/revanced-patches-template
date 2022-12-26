package app.revanced.patches.youtube.misc.doublebacktoclose.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.doublebacktoclose.annotations.DoubleBackToCloseCompatibility
import app.revanced.patches.youtube.misc.doublebacktoclose.fingerprint.OnBackPressedFingerprint
import app.revanced.patches.youtube.misc.doublebacktoclose.fingerprint.ScrollPositionFingerprint
import app.revanced.patches.youtube.misc.doublebacktoclose.fingerprint.ScrollTopFingerprint
import app.revanced.patches.youtube.misc.doublebacktoclose.fingerprint.ScrollTopParentFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("double-back-to-close")
@Description("Close the app by double-tapping the back button from the home feed.")
@DoubleBackToCloseCompatibility
@Version("0.0.1")
class DoubleBackToClosePatch : BytecodePatch(
    listOf(
        OnBackPressedFingerprint,
        ScrollPositionFingerprint,
        ScrollTopParentFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_enable_double_back_to_close",
                StringResource("revanced_enable_double_back_to_close_title", "Enable double back to close"),
                true,
                StringResource("revanced_enable_double_back_to_close_summary_on", "Double back to close is enabled"),
                StringResource("revanced_enable_double_back_to_close_summary_off", "Double back to close is disabled")
            )
        )


        /*
        Hook onBackPressed method inside WatchWhileActivity
         */
        OnBackPressedFingerprint.result?.let { result ->
            val insertIndex = result.scanResult.patternScanResult!!.endIndex

            with(result.mutableMethod) {
                addInstruction(
                    insertIndex,
                    "invoke-static {p0}, $INTEGRATIONS_CLASS_DESCRIPTOR" +
                    "->" +
                    "closeActivityOnBackPressed(Lcom/google/android/apps/youtube/app/watchwhile/WatchWhileActivity;)V"
                )
            }
        } ?: return OnBackPressedFingerprint.toErrorResult()


        /*
        Inject the methods which start of ScrollView
         */
        ScrollPositionFingerprint.result?.let { result ->
            val insertMethod = context.toMethodWalker(result.method)
                .nextMethod(result.scanResult.patternScanResult!!.startIndex + 1, true)
                .getMethod() as MutableMethod

            val insertIndex = insertMethod.implementation!!.instructions.size - 1 - 1

            injectScrollView(insertMethod, insertIndex, "onStartScrollView")
        } ?: return ScrollPositionFingerprint.toErrorResult()


        /*
        Inject the methods which stop of ScrollView
         */
        ScrollTopParentFingerprint.result?.let { parentResult ->
            ScrollTopFingerprint.also { it.resolve(context, parentResult.classDef) }.result?.let { result ->
                val insertMethod = result.mutableMethod
                val insertIndex = result.scanResult.patternScanResult!!.endIndex

                injectScrollView(insertMethod, insertIndex, "onStopScrollView")
            } ?: return ScrollTopFingerprint.toErrorResult()
        } ?: return ScrollTopParentFingerprint.toErrorResult()

        return PatchResultSuccess()
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/DouBleBackToClosePatch;"

        fun injectScrollView(
            method: MutableMethod,
            index: Int,
            descriptor: String
        ) {
            method.addInstruction(
                index,
                "invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->$descriptor()V"
            )
        }
    }
}
