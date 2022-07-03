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
import java.io.OutputStream
import java.nio.file.Files

@Name("sponsorblock-resource-patch")
@Dependencies(dependencies = [FixLocaleConfigErrorPatch::class])
@SponsorBlockCompatibility
@Version("0.0.1")
class SponsorblockResourcePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        val classLoader = this.javaClass.classLoader

        // merge sponsorblock strings to main strings
        val stringsResourcePath = "values/strings.xml"
        val stringsResourceInputStream = classLoader.getResourceAsStream("sponsorblock/$stringsResourcePath")!!

        data.xmlEditor["res/$stringsResourcePath"].use { destinationStringsResource ->
            val destination = destinationStringsResource.file
            val destinationNode = destination.getElementsByTagName("resources")
                .item(0)

            data.xmlEditor[stringsResourceInputStream, OutputStream.nullOutputStream()].use { sponsorblockStringsResource ->
                val sponsorblockStringNodes =
                    sponsorblockStringsResource.file.getElementsByTagName("resources").item(0).childNodes

                for (index in 0 until sponsorblockStringNodes.length) {
                    val node = sponsorblockStringNodes.item(index).cloneNode(true)
                    destination.adoptNode(node)
                    destinationNode.appendChild(node)
                }
            }
        }

        // merge sponsorblock drawables to main drawables
        val drawables = "drawable" to arrayOf(
            "ic_sb_adjust",
            "ic_sb_compare",
            "ic_sb_edit",
            "ic_sb_logo",
            "ic_sb_publish",
            "ic_sb_voting"
        )
        val layouts = "layout" to arrayOf(
            "inline_sponsor_overlay", "new_segment", "skip_sponsor_button"
        )

        // collect resources
        val xmlResources = arrayOf(drawables, layouts)
        xmlResources.forEach { (path, resourceNames) ->
            resourceNames.forEach { name ->
                val relativePath = "$path/$name.xml"

                Files.copy(
                    classLoader.getResourceAsStream("sponsorblock/$relativePath")!!,
                    data["res"].resolve(relativePath).toPath()
                )
            }
        }

        return PatchResultSuccess()
    }
}