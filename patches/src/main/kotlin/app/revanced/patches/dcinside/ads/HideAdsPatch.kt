package app.revanced.patches.dcinside.ads

import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.util.returnEarly

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide ads",
    description = "Hide ads across the app.",
) {
    compatibleWith("com.dcinside.app.android")

    apply {
        shouldLoadAdMethod.addInstructions(
            0,
            """
                new-instance v0, Ljava/util/ArrayList;
                invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V
                return-object v0
            """
        )

        shouldShowFooterAdMethod.returnEarly()
        setMinimumHeightMethod.returnEarly(0)
    }
}
