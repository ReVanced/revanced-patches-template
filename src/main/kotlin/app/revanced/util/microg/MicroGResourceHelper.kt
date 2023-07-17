package app.revanced.util.microg

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.apk.Apk
import app.revanced.util.resources.ResourceUtils.editText
import app.revanced.util.resources.ResourceUtils.mergeStrings

/**
 * Helper class for applying resource patches needed for the microg-support patches.
 */
internal object MicroGResourceHelper {
    private val strings = mapOf(
        "microg_not_installed_warning" to "Vanced MicroG is not installed. Please install it.",
        "microg_not_running_warning" to "Vanced MicroG is failing to run. Please follow the \"Don't kill my app\" guide for Vanced MicroG.",
    )

    /**
     * Add necessary strings to the strings.xml file.
     *
     * @param context The resource context.
     */
    fun addStrings(context: ResourceContext) =
        context.mergeStrings(strings)

    /**
     * Patch the manifest to work with MicroG.
     *
     * @param context The resource context.
     * @param fromPackageName The original package name.
     * @param toPackageName The package name to accept.
     * @param toName The new name of the app.
     */
    fun patchManifest(
        context: ResourceContext,
        fromPackageName: String,
        toPackageName: String,
        toName: String
    ) {
        fun Apk.transform() {
            resources.openFile(Apk.MANIFEST_FILE_NAME).editText { txt ->
                if (this@transform is Apk.Base) {
                    // in the case of the base apk additional transformations are needed
                    txt.replace(
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
                        "$fromPackageName.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
                        "$toPackageName.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
                    ).replace(
                        "com.google.android.c2dm",
                        "${Constants.MICROG_VENDOR}.android.c2dm"
                    ).replace(
                        "</queries>",
                        "<package android:name=\"${Constants.MICROG_VENDOR}.android.gms\"/></queries>"
                    )
                } else {
                    txt
                }.replace(
                    "package=\"$fromPackageName",
                    "package=\"$toPackageName"
                )
            }
        }

        context.apkBundle.forEach(Apk::transform)
    }
}
