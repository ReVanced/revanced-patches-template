package app.revanced.patches.dcinside.post.view

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.util.returnEarly

@Suppress("unused")
val hideBottomLikePostsBytecodePatch = bytecodePatch(
    name = "Hide bottom like posts",
    description = "Hides recommended posts below the next/previous post.",
) {
    compatibleWith("com.dcinside.app.android")

    apply {
        bottomLikePostsMethod.returnEarly()
    }
}