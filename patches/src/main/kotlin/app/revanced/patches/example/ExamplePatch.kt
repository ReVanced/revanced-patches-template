package app.revanced.patches.example

import app.revanced.patcher.patch.bytecodePatch

@Suppress("unused")
val examplePatch = bytecodePatch(
    name = "Example Patch",
    description = "This is an example patch to start with.",
) {
    compatibleWith("com.example.app"("1.0.0"))

    extendWith("extensions/extension.rve")

    execute {
        // TODO("Not yet implemented")
    }
}
