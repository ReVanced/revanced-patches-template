package app.revanced.patches.youtube.interaction.downloads

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.misc.playercontrols.PlayerControlsBytecodePatch
import app.revanced.patches.youtube.video.information.VideoInformationPatch

@Patch(
    name = "External downloads",
    description = "Adds support to download and save YouTube videos using an external app.",
    dependencies = [
        ExternalDownloadsResourcePatch::class,
        PlayerControlsBytecodePatch::class,
        VideoInformationPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.32.39",
                "18.37.36",
                "18.38.44",
                "18.43.45",
                "18.44.41",
                "18.45.41",
                "18.45.43"
            ]
        ),
    ]
)
@Suppress("unused")
object ExternalDownloadsBytecodePatch : BytecodePatch() {
    private const val BUTTON_DESCRIPTOR = "Lapp/revanced/integrations/videoplayer/ExternalDownloadButton;"

    override fun execute(context: BytecodeContext) {
        /*
        initialize the control
         */

        PlayerControlsBytecodePatch.initializeControl(
            "$BUTTON_DESCRIPTOR->initializeButton(Landroid/view/View;)V")

        /*
         add code to change the visibility of the control
         */

        PlayerControlsBytecodePatch.injectVisibilityCheckCall(
            "$BUTTON_DESCRIPTOR->changeVisibility(Z)V")
    }
}