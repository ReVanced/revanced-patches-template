package app.revanced.patches.dcinside.home

import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.patch.resourcePatch
import app.revanced.patches.dcinside.misc.extension.sharedExtensionPatch
import app.revanced.util.doRecursively
import org.w3c.dom.Element

internal val hideDcbest = booleanOption(
    default = true,
    name = "Hide dcbest",
    description = "Permanently hide tab of dcbest",
)
internal val hideRecommendedGalleries = booleanOption(
    default = true,
    name = "Hide recommended galleries",
    description = "Permanently hide recommended galleries",
)
internal val hideCrowdGalleries = booleanOption(
    default = true,
    name = "Hide crowd galleries",
    description = "Permanently hide crowd galleries",
)
internal val hideNewGallery = booleanOption(
    default = true,
    name = "Hide new gallery",
    description = "Permanently hide new gallery",
)
internal val hideShortcutGalleries = booleanOption(
    default = false,
    name = "Hide shortcut galleries",
    description = "Permanently hide shortcut galleries",
)

@Suppress("unused")
private val hideHomeElementBytecodePatch = bytecodePatch {
    compatibleWith("com.dcinside.app.android")
    apply {
        val hideNewGallery by hideNewGallery

        if (hideNewGallery!!) {
            mainSetNewGalleriesMethod.addInstructions(
                0,
                """
                    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;
                    move-result-object p1
                """
            )
        }
    }
}

@Suppress("unused")
val hideHomeElementResourcePatch = resourcePatch(
    name = "Hide home elements",
    description = "Hide home elements of app permanently. DCbest is hidden by default.",
    use = false
) {
    compatibleWith("com.dcinside.app.android")
    dependsOn(
        sharedExtensionPatch,
        hideHomeElementBytecodePatch,
    )
    val hideDcbest by hideDcbest
    val hideRecommendedGalleries by hideRecommendedGalleries
    val hideCrowdGalleries by hideCrowdGalleries
    val hideNewGallery by hideNewGallery

    val hideShortcutGalleries by hideShortcutGalleries

    apply {
        fun Element.hideElement() {
            this.setAttribute("android:visibility", "gone")
            this.setAttribute("android:layout_height", "0dp")
            this.setAttribute("android:layout_width", "0dp")
            this.setAttribute("android:maxHeight", "0dp")
            this.setAttribute("android:maxWidth", "0dp")
        }

        if (hideDcbest!!) {
            listOf(
                "res/layout/view_main_best_filter.xml", // dcbest filter
                "res/layout/view_live_best_item.xml", // dcbest posts
                "res/layout/view_main_live_best_more.xml", // dcbest shortcut
                "res/layout/view_main_bottom.xml", // main bottom
            ).forEach { document(it).use { document -> document.documentElement.hideElement() } }
        }

        if (hideRecommendedGalleries!!) {
            document("res/layout/view_recommend_galleries.xml").use { document -> document.documentElement.hideElement() }
        }

        if (hideCrowdGalleries!!) {
            document("res/layout/view_crowd.xml").use { document ->
                val root = document.documentElement ?: return@use

                root.doRecursively { node ->
                    if (node is Element) {
                        node.hideElement()
                    }
                }
            }
        }

        listOf(
            "res/layout/view_recent_basic.xml",
            "res/layout/view_recent_split.xml",
        ).forEach {
            document(it).use { document ->
                if (hideShortcutGalleries!!) {
                    document.documentElement.hideElement()
                }
            }
        }
    }
}