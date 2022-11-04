package app.revanced.patches.youtube.layout.pivotbar.shortsbutton.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.MethodFingerprintExtensions.name
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.pivotbar.fingerprints.PivotBarFingerprint
import app.revanced.patches.youtube.layout.pivotbar.resource.patch.PivotBarResourcePatch
import app.revanced.patches.youtube.layout.pivotbar.annotations.PivotBarCompatibility
import app.revanced.patches.youtube.layout.pivotbar.shortsbutton.fingerprints.PivotBarEnumFingerprint
import app.revanced.patches.youtube.layout.pivotbar.shortsbutton.fingerprints.PivotBarShortsButtonViewFingerprint
import app.revanced.patches.youtube.layout.pivotbar.utils.InjectionUtils.REGISTER_TEMPLATE_REPLACEMENT
import app.revanced.patches.youtube.layout.pivotbar.utils.InjectionUtils.injectHook
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch

@Patch
@DependsOn([IntegrationsPatch::class, PivotBarResourcePatch::class])
@Name("hide-shorts-button")
@Description("Hides the shorts button on the navigation bar.")
@PivotBarCompatibility
@Version("0.0.1")
class ShortsButtonRemoverPatch : BytecodePatch(
    listOf(PivotBarFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        /*
         * Resolve fingerprints
         */

        val pivotBarResult = PivotBarFingerprint.result ?: return PatchResultError("PivotBarFingerprint failed")
        val fingerprintResults = arrayOf(PivotBarEnumFingerprint, PivotBarShortsButtonViewFingerprint)
            .onEach {
                val resolutionSucceeded = it.resolve(
                    context,
                    pivotBarResult.mutableMethod,
                    pivotBarResult.mutableClass
                )

                if (!resolutionSucceeded) return PatchResultError("${it.name} failed")
            }
            .map { it.result!!.scanResult.patternScanResult!! }

        val enumScanResult = fingerprintResults[0]
        val buttonViewResult = fingerprintResults[1]

        val enumHookInsertIndex = enumScanResult.startIndex + 2
        val buttonHookInsertIndex = buttonViewResult.endIndex

        /*
         * Inject hooks
         */

        val integrationsClass = "Lapp/revanced/integrations/patches/HideShortsButtonPatch;"

        val enumHook =
            "sput-object v$REGISTER_TEMPLATE_REPLACEMENT, $integrationsClass->lastPivotTab:Ljava/lang/Enum;"
        val buttonHook =
            "invoke-static { v$REGISTER_TEMPLATE_REPLACEMENT }, $integrationsClass->hideShortsButton(Landroid/view/View;)V"

        // Inject bottom to top to not mess up the indices
        mapOf(
            buttonHook to buttonHookInsertIndex,
            enumHook to enumHookInsertIndex
        ).forEach { (hook, insertIndex) ->
            pivotBarResult.mutableMethod.injectHook(hook, insertIndex)
        }

        return PatchResultSuccess()
    }
}