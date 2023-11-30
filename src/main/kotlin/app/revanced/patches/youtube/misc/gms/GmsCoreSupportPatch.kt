package app.revanced.patches.youtube.misc.gms

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patches.shared.misc.gms.AbstractGmsCoreSupportPatch
import app.revanced.patches.youtube.layout.buttons.cast.HideCastButtonPatch
import app.revanced.patches.youtube.misc.fix.playback.ClientSpoofPatch
import app.revanced.patches.youtube.misc.gms.Constants.REVANCED_YOUTUBE_PACKAGE_NAME
import app.revanced.patches.youtube.misc.gms.Constants.YOUTUBE_PACKAGE_NAME
import app.revanced.patches.youtube.misc.gms.GmsCoreSupportResourcePatch.gmsCoreVendorOption
import app.revanced.patches.youtube.misc.gms.fingerprints.*
import app.revanced.patches.youtube.shared.fingerprints.WatchWhileActivityFingerprint


@Suppress("unused")
object GmsCoreSupportPatch : AbstractGmsCoreSupportPatch(
    fromPackageName = YOUTUBE_PACKAGE_NAME,
    toPackageName = REVANCED_YOUTUBE_PACKAGE_NAME,
    primeMethodFingerprint = PrimeMethodFingerprint,
    earlyReturnFingerprints = setOf(
        ServiceCheckFingerprint,
        GooglePlayUtilityFingerprint,
        CastDynamiteModuleFingerprint,
        CastDynamiteModuleV2Fingerprint,
        CastContextFetchFingerprint
    ),
    dependencies = setOf(
        HideCastButtonPatch::class,
        ClientSpoofPatch::class
    ),
    abstractGmsCoreSupportResourcePatch = GmsCoreSupportResourcePatch,
    compatiblePackages = setOf(
        CompatiblePackage(
            "com.google.android.youtube", setOf(
                "18.16.37",
                "18.19.35",
                "18.20.39",
                "18.23.35",
                "18.29.38",
                "18.32.39",
                "18.37.36",
                "18.38.44",
                "18.43.45",
                "18.44.41",
                "18.45.41"
            )
        )
    ),
    fingerprints = setOf(
        ServiceCheckFingerprint,
        GooglePlayUtilityFingerprint,
        CastDynamiteModuleFingerprint,
        CastDynamiteModuleV2Fingerprint,
        CastContextFetchFingerprint,
        PrimeMethodFingerprint,
        WatchWhileActivityFingerprint
    )
) {
    override val gmsCoreVendor = gmsCoreVendorOption.value!!

    override fun execute(context: BytecodeContext) {
        // Check the availability of GmsCore.
        WatchWhileActivityFingerprint.result?.mutableMethod?.addInstruction(
            0,
            "invoke-static {}, Lapp/revanced/integrations/patches/GmsCoreSupport;->checkAvailability()V"
        ) ?: throw WatchWhileActivityFingerprint.exception

        super.execute(context)
    }
}
