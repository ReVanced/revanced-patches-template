package app.revanced.patches.youtube.layout.sponsorblock.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.DomFileEditor
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.layout.sponsorblock.annotations.SponsorBlockCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.Preference
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import java.nio.file.Files

@Name("sponsorblock-resource-patch")
@SponsorBlockCompatibility
@DependsOn([FixLocaleConfigErrorPatch::class, SettingsPatch::class])
@Version("0.0.1")
class SponsorBlockResourcePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        val youtubePackage = "com.google.android.youtube"
        SettingsPatch.addPreference(
            Preference(
                StringResource("sb_settings", "SponsorBlock Settings"),
                Preference.Intent(
                    youtubePackage,
                    "sponsorblock_settings",
                    "com.google.android.libraries.social.licenses.LicenseActivity"
                ),
                StringResource("revanced_sponsorblock_settings_summary", "SponsorBlock related settings"),
            )
        )
        val classLoader = this.javaClass.classLoader

        /*
         merge SponsorBlock strings to main strings
         */
        val stringsResourcePath = "host/values/strings.xml"
        val stringsResourceInputStream = classLoader.getResourceAsStream("sponsorblock/$stringsResourcePath")!!

        data.xmlEditor[stringsResourceInputStream].use {
            val stringsNode = it.file.getElementsByTagName("resources").item(0).childNodes

            for (i in 1 until stringsNode.length - 1) {
                val stringNode = stringsNode.item(i)

                // TODO: figure out why this is needed
                if (!stringNode.hasAttributes()) continue

                val attributes = stringNode.attributes
                val key = attributes.getNamedItem("name")!!.nodeValue!!
                val value = stringNode.textContent!!

                // all strings of SponsorBlock which have this attribute have the attribute value false,
                // hence a null check suffices
                val formatted = attributes.getNamedItem("formatted") == null

                SettingsPatch.addString(key, value, formatted)
            }
        }

        /*
         merge SponsorBlock drawables to main drawables
         */
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

        // write resources
        xmlResources.forEach { (path, resourceNames) ->
            resourceNames.forEach { name ->
                val relativePath = "$path/$name.xml"

                Files.copy(
                    classLoader.getResourceAsStream("sponsorblock/$relativePath")!!,
                    data["res"].resolve(relativePath).toPath()
                )
            }
        }

        /*
        merge xml nodes from the host to their real xml files
         */

        // collect all host resources
        val hostingXmlResources = mapOf("layout" to arrayOf("youtube_controls_layout"))

        // copy nodes from host resources to their real xml files
        hostingXmlResources.forEach { (path, resources) ->
            resources.forEach { resource ->
                val hostingResourceStream = classLoader.getResourceAsStream("sponsorblock/host/$path/$resource.xml")!!

                val targetXmlEditor = data.xmlEditor["res/$path/$resource.xml"]
                "RelativeLayout".copyXmlNode(
                    data.xmlEditor[hostingResourceStream],
                    targetXmlEditor
                ).also {
                    val children = targetXmlEditor.file.getElementsByTagName("RelativeLayout").item(0).childNodes

                    // Replace the startOf with the voting button view so that the button does not overlap
                    for (i in 1 until children.length) {
                        val view = children.item(i)

                        // Replace the attribute for a specific node only
                        if (!(view.hasAttributes() && view.attributes.getNamedItem("android:id").nodeValue.endsWith("live_chat_overlay_button"))) continue

                        // voting button id from the voting button view from the youtube_controls_layout.xml host file
                        val votingButtonId = "@+id/voting_button"

                        view.attributes.getNamedItem("android:layout_toStartOf").nodeValue = votingButtonId

                        break
                    }
                }.close() // close afterwards
            }
        }
        return PatchResultSuccess()
    }

    /**
     * Copies the specified node of the source [DomFileEditor] to the target [DomFileEditor].
     * @param source the source [DomFileEditor].
     * @param target the target [DomFileEditor]-
     */
    private fun String.copyXmlNode(source: DomFileEditor, target: DomFileEditor): AutoCloseable {
        val hostNodes = source.file.getElementsByTagName(this).item(0).childNodes

        val destinationResourceFile = target.file
        val destinationNode = destinationResourceFile.getElementsByTagName(this).item(0)

        for (index in 0 until hostNodes.length) {
            val node = hostNodes.item(index).cloneNode(true)
            destinationResourceFile.adoptNode(node)
            destinationNode.appendChild(node)
        }

        return AutoCloseable {
            source.close()
            target.close()
        }
    }
}