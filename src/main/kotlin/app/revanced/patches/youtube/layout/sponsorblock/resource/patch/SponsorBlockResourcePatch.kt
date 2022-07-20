package app.revanced.patches.youtube.layout.sponsorblock.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.layout.sponsorblock.annotations.SponsorBlockCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResource
import app.revanced.util.resources.ResourceUtils.copyXmlNode
import java.io.OutputStream

@Name("sponsorblock-resource-patch")
@SponsorBlockCompatibility
@Dependencies([FixLocaleConfigErrorPatch::class])
@Version("0.0.1")
class SponsorBlockResourcePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        val classLoader = this.javaClass.classLoader

        /*
         * Copy SponsorBlock strings
         */

        data.copyXmlNode(
            "sponsorblock",
            "values/strings.xml",
            "resources"
        )

        /*
         * Copy SponsorBlock resources
         */

        data.copyResource(
            "sponsorblock",
            ResourceUtils.ResourceGroup(
                "drawable",

                "ic_sb_adjust.xml",
                "ic_sb_compare.xml",
                "ic_sb_edit.xml",
                "ic_sb_logo.xml",
                "ic_sb_publish.xml",
                "ic_sb_voting.xml"
            ),
            ResourceUtils.ResourceGroup(
                "layout",
                "inline_sponsor_overlay.xml",
                "new_segment.xml",
                "skip_sponsor_button.xml"
            )
        )

        /*
         * Merge xml nodes from the host to their real xml files
         */

        // Collect all host resources
        val hostingXmlResources = mapOf("layout" to arrayOf("youtube_controls_layout"))

        // Copy nodes from host resources to their real xml files
        hostingXmlResources.forEach { (path, resources) ->
            resources.forEach { resource ->
                val hostingResourceStream = classLoader.getResourceAsStream("sponsorblock/host/$path/$resource.xml")!!

                val targetXmlEditor = data.xmlEditor["res/$path/$resource.xml"]
                "RelativeLayout".copyXmlNode(
                    data.xmlEditor[hostingResourceStream, OutputStream.nullOutputStream()],
                    targetXmlEditor
                ).also {
                    val children = targetXmlEditor.file.getElementsByTagName("RelativeLayout").item(0).childNodes

                    // Replace the startOf with the voting button view so that the button does not overlap
                    for (i in 1 until children.length) {
                        val view = children.item(i)

                        // Replace the attribute for a specific node only
                        if (!(view.hasAttributes() && view.attributes.getNamedItem("android:id").nodeValue.endsWith("live_chat_overlay_button"))) continue

                        // Voting button id from the voting button view from the youtube_controls_layout.xml host file
                        val votingButtonId = "@+id/voting_button"

                        view.attributes.getNamedItem("android:layout_toStartOf").nodeValue = votingButtonId

                        break
                    }
                }.close() // Close afterwards
            }
        }
        return PatchResultSuccess()
    }
}