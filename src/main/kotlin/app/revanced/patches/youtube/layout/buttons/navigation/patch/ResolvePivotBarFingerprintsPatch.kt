package app.revanced.patches.youtube.layout.buttons.navigation.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.youtube.layout.buttons.navigation.fingerprints.InitializeButtonsFingerprint
import app.revanced.patches.youtube.layout.buttons.navigation.fingerprints.PivotBarConstructorFingerprint

@DependsOn([ResourceMappingPatch::class])
@Description("Resolves necessary fingerprints.")
@Version("0.0.1")
class ResolvePivotBarFingerprintsPatch : BytecodePatch(
    listOf(PivotBarConstructorFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        // imageOnlyTabResourceId is used in InitializeButtonsFingerprint fingerprint
        imageOnlyTabResourceId = ResourceMappingPatch.resourceIdOf("layout", "image_only_tab")

        PivotBarConstructorFingerprint.result?.let {
            // Resolve InitializeButtonsFingerprint on the class of the method
            // which PivotBarConstructorFingerprint resolved to
            if (!InitializeButtonsFingerprint.resolve(
                    context,
                    it.classDef
                )
            ) InitializeButtonsFingerprint.error()
        } ?: PivotBarConstructorFingerprint.error()
    }

    internal companion object {
        var imageOnlyTabResourceId: Long = -1
    }
}