package app.revanced.patches.dcinside.misc.signature

import app.revanced.patcher.classDef
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.dcinside.misc.extension.sharedExtensionPatch

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/revanced/extension/dcinside/patches/SpoofSignaturePatch;"

@Suppress("unused")
val spoofSignaturePatch = bytecodePatch(
    name = "Spoof signature",
    description = "Spoofs the signature of the app to fix issues with receiving notifications."
) {
    compatibleWith("com.dcinside.app.android")
    dependsOn(sharedExtensionPatch)

    apply {
        applicationMethod.classDef.setSuperClass(EXTENSION_CLASS_DESCRIPTOR)
    }
}
