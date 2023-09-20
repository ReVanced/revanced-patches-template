package app.revanced.patches.music.misc.microg

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.music.misc.microg.fingerprints.*
import app.revanced.patches.music.misc.microg.shared.Constants.MUSIC_PACKAGE_NAME
import app.revanced.patches.music.misc.microg.shared.Constants.REVANCED_MUSIC_PACKAGE_NAME
import app.revanced.patches.youtube.misc.microg.shared.Constants
import app.revanced.util.microg.MicroGBytecodeHelper


@Patch(
    name = "MicroG support",
    description = "Allows YouTube Music ReVanced to run without root and under a different package name.",
    dependencies = [MicroGResourcePatch::class],
    compatiblePackages = [CompatiblePackage("com.google.android.apps.youtube.music")]
)
@Suppress("unused")
object MicroGBytecodePatch : BytecodePatch(
    setOf(
        ServiceCheckFingerprint,
        GooglePlayUtilityFingerprint,
        CastDynamiteModuleFingerprint,
        CastDynamiteModuleV2Fingerprint,
        CastContextFetchFingerprint,
        PrimeFingerprint,
    )
) {
    // NOTE: the previous patch also replaced the following strings, but it seems like they are not needed:
    // - "com.google.android.gms.chimera.GmsIntentOperationService",
    // - "com.google.android.gms.phenotype.internal.IPhenotypeCallbacks",
    // - "com.google.android.gms.phenotype.internal.IPhenotypeService",
    // - "com.google.android.gms.phenotype.PACKAGE_NAME",
    // - "com.google.android.gms.phenotype.UPDATE",
    // - "com.google.android.gms.phenotype",
    override fun execute(context: BytecodeContext) = MicroGBytecodeHelper.patchBytecode(
        context,
        arrayOf(
            MicroGBytecodeHelper.packageNameTransform(
                Constants.PACKAGE_NAME,
                Constants.REVANCED_PACKAGE_NAME
            )
        ),
        MicroGBytecodeHelper.PrimeMethodTransformationData(
            PrimeFingerprint,
            MUSIC_PACKAGE_NAME,
            REVANCED_MUSIC_PACKAGE_NAME
        ),
        listOf(
            ServiceCheckFingerprint,
            GooglePlayUtilityFingerprint,
            CastDynamiteModuleFingerprint,
            CastDynamiteModuleV2Fingerprint,
            CastContextFetchFingerprint
        )
    )
}
