package app.revanced.patches.dcinside.misc.hook

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.dcinside.misc.hook.json.addJsonHook
import app.revanced.patches.dcinside.misc.hook.json.jsonHook
import app.revanced.patches.dcinside.misc.hook.json.jsonHookPatch

fun hookPatch(
    name: String,
    hookClassDescriptor: String,
) = bytecodePatch(name) {
    dependsOn(jsonHookPatch)
    compatibleWith("com.dcinside.app.android")

    apply {
        addJsonHook(jsonHook(hookClassDescriptor))
    }
}