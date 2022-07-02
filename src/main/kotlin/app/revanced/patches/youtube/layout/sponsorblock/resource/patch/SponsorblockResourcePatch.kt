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
import kotlin.io.path.Path

@Name("sponsorblock-resource-patch")
@Dependencies(dependencies = [FixLocaleConfigErrorPatch::class])
@SponsorBlockCompatibility
@Version("0.0.1")
class SponsorblockResourcePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        val classLoader = this.javaClass.classLoader

        // merge sponsorblock strings to main strings
        val stringsResourcePath = Path("values").resolve("strings.xml").toString()
        val stringsResourceInputStream =
            classLoader.getResourceAsStream(Path("sponsorblock").resolve(stringsResourcePath).toString())!!

        data.xmlEditor["res/$stringsResourcePath"].use { destinationStringsResource ->
            data.xmlEditor[stringsResourceInputStream, OutputStream.nullOutputStream()].use { sponsorblockStringsResource ->
                val sponsorblockStringNodes = sponsorblockStringsResource.file.getElementsByTagName("resources").item(0).childNodes

                for (index in 0..sponsorblockStringNodes.length) {
                    destinationStringsResource.file.getElementsByTagName("resources").item(0).appendChild(
                        sponsorblockStringNodes.item(index)
                    )
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
            "ic_sb_voting",
            "player_fast_forward",
            "player_fast_rewind"
        )
        val layouts = "layout" to arrayOf(
            "inline_sponsor_overlay", "new_segment", "skip_sponsor_button"
        )
        val hdpiDrawables = "drawable-xxxhdpi" to arrayOf(
            "quantum_ic_fast_forward_grey600_36",
            "quantum_ic_fast_forward_white_36",
            "quantum_ic_fast_rewind_grey600_36",
            "quantum_ic_fast_rewind_white_36"
        )

        // collect resources
        val xmlResources = arrayOf(drawables, layouts)
        val resources = arrayOf(hdpiDrawables)

        // write xml resources
        xmlResources.forEach { (xmlResourcePath, xmlResources) ->
            classLoader.writeResources(xmlResourcePath, xmlResources, ResourceType.Xml)
        }

        // write resources
        resources.forEach { (resourcePath, resources) ->
            classLoader.writeResources(resourcePath, resources, ResourceType.Resource)
        }

        return PatchResultSuccess()
    }

    /**
     * Writes a list of resources of sponsorblock to the given path.
     * @param destination The path to write the resources to.
     * @param resources The resources to write.
     * @param resourceType The type of the resources.
     */
    private fun ClassLoader.writeResources(destination: String, resources: Array<String>, resourceType: ResourceType) {
        val extension = when (resourceType) {
            ResourceType.Resource -> "png"
            ResourceType.Xml -> "xml"
        }

        for (resource in resources) {
            val resourcePath = Path(destination, "$resource.$extension").toString()
            val destinationPath = Path("res", resourcePath)

            this.getResourceAsStream("sponsorblock/$resourcePath")!!.readAllBytes()
                .let { Files.write(destinationPath, it) }
        }
    }

    private enum class ResourceType {
        Resource, Xml
    }
}