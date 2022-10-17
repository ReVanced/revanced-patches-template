package app.revanced.util.microg

import app.revanced.patcher.ResourceContext

/**
 * Helper class for applying resource patches needed for the microg-support patches.
 */
internal object MicroGResourceHelper {
    /**
     * Patch the manifest to work with MicroG.
     *
     * @param context Bytecode context.
     * @param fromPackageName Original package name.
     * @param toPackageName The package name to accept.
     * @param toName The new name of the app.
     */
    fun patchManifest(
        context: ResourceContext,
        fromPackageName: String,
        toPackageName: String,
        toName: String
    ) {
        // patch the manifest in all manifest files if present
        ResourceContext.ApkContext.values().forEach { apkContext ->
            with(context.getFile("AndroidManifest.xml", apkContext)) {
                if (apkContext == ResourceContext.ApkContext.BASE) {
                    // in the case of the base apk additional transformations are needed
                    this.readText().replace(
                        "android:label=\"@string/app_name",
                        "android:label=\"$toName"
                    ).replace(
                        "android:label=\"@string/app_launcher_name",
                        "android:label=\"$toName"
                    ).replace(
                        "android:authorities=\"$fromPackageName",
                        "android:authorities=\"$toPackageName"
                    ).replace(
                        "$fromPackageName.permission.C2D_MESSAGE",
                        "$toPackageName.permission.C2D_MESSAGE"
                    ).replace(
                        "com.google.android.c2dm",
                        "${Constants.MICROG_VENDOR}.android.c2dm"
                    ).replace(
                        "</queries>",
                        "<package android:name=\"${Constants.MICROG_VENDOR}.android.gms\"/></queries>"
                    )
                } else {
                    // the other splits might not exist in the current context, in that case simply continue with the next apk
                    if (!this.exists()) return

                    this.readText()
                }.replace(
                    "package=\"$fromPackageName",
                    "package=\"$toPackageName"
                ).let(this::writeText)
            }
        }
    }
}