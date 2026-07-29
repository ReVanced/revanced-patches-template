package app.revanced.patches.shared.misc.gms

import app.revanced.patcher.*
import app.revanced.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.ClassDef

internal val BytecodePatchContext.googlePlayUtilityMethod by gettingFirstMethodDeclarativelyOrNull(
    "This should never happen.",
    "MetadataValueReader",
    "com.google.android.gms",
) {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returnType("I")
    parameterTypes("L", "I")
}

/**
 * The `GooglePlayServicesUtilLight.isGooglePlayServicesAvailable(Context, int)` method.
 *
 * Some apps bundle a Maps SDK that gates map initialization on this availability check instead of
 * the `GoogleApiAvailabilityLight` method matched by [googlePlayUtilityMethod]. For example, Google
 * Photos' "Map"/"Places" feature crashes with `IBitmapDescriptorFactory is not initialized` because
 * this check fails for GmsCore (which is not Google-signed).
 */
internal val BytecodePatchContext.isGooglePlayServicesAvailableMethod by gettingFirstMethodDeclarativelyOrNull(
    "com.google.android.gms.version",
    "com.google.app.id",
)

internal val BytecodePatchContext.serviceCheckMethod by gettingFirstMethodDeclaratively(
    "Google Play Services not available",
) {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returnType("V")
    parameterTypes("L", "I")
}

internal val BytecodePatchContext.getGmsCoreVendorGroupIdMethod by gettingFirstMethodDeclaratively {
    name("getGmsCoreVendorGroupId")
    definingClass(EXTENSION_CLASS_DESCRIPTOR)
    accessFlags(AccessFlags.PRIVATE, AccessFlags.STATIC)
    returnType("Ljava/lang/String;")
    parameterTypes()
}

internal val BytecodePatchContext.originalPackageNameExtensionMethod by gettingFirstMethodDeclaratively {
    name("getOriginalPackageName")
    definingClass(EXTENSION_CLASS_DESCRIPTOR)
    accessFlags(AccessFlags.PRIVATE, AccessFlags.STATIC)
    returnType("Ljava/lang/String;")
    parameterTypes()
}
