package app.revanced.patches.dcinside.misc.update

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.util.returnEarly

@Suppress("unused")
val disableUpdateCheckPatch = bytecodePatch(
    name = "Disable update check",
    description = "Disable update check",
    use = false,
) {
    compatibleWith("com.dcinside.app.android")

    apply {
        updateCheckMethod.returnEarly(false)
    }
}