package app.revanced.patches.youtube.misc.playercontrols.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.DomFileEditor
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.interaction.downloads.annotation.DownloadsCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import java.io.OutputStream

@Name("bottom-controls-resource-patch")
@Dependencies([FixLocaleConfigErrorPatch::class])
@Description("Manages the resources for the bottom controls of the YouTube player.")
@DownloadsCompatibility
@Version("0.0.1")
class BottomControlsResourcePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        resourceData = data
        resourceFileEditor = data.xmlEditor[TARGET_RESOURCE]

        return PatchResultSuccess()
    }

    companion object {
        internal const val TARGET_RESOURCE_NAME = "youtube_controls_bottom_ui_container.xml"
        internal const val TARGET_RESOURCE = "res/layout/$TARGET_RESOURCE_NAME"

        private lateinit var resourceData: ResourceData
        private lateinit var resourceFileEditor: DomFileEditor

        // The element to which to add the new elements to
        private var lastLeftOf = "fullscreen_button"

        /**
         * Add new controls to the bottom of the YouTube player.
         * @param hostYouTubeControlsBottomUiResourceName The hosting resource name containing the elements.
         */
        internal fun addControls(hostYouTubeControlsBottomUiResourceName: String) {
            val sourceXmlEditor =
                resourceData.xmlEditor[
                        this::class.java.classLoader.getResourceAsStream(hostYouTubeControlsBottomUiResourceName)!!,
                        OutputStream.nullOutputStream()
                ]
            val targetXmlEditor =
                resourceData.xmlEditor[TARGET_RESOURCE]
            
            val targetElement =
                "android.support.constraint.ConstraintLayout"

            val hostElements = sourceXmlEditor.file.getElementsByTagName(targetElement).item(0).childNodes

            val destinationResourceFile = targetXmlEditor.file
            val destinationElement =
                destinationResourceFile.getElementsByTagName(targetElement).item(0)

            for (index in 1 until hostElements.length) {
                val element = hostElements.item(index).cloneNode(true)

                // if the element has no attributes theres no point to adding it to the destination
                if (!element.hasAttributes()) continue

                // set the elements lastLeftOf attribute to the lastLeftOf value
                val namespace = "@+id"
                element.attributes.getNamedItem("yt:layout_constraintRight_toLeftOf").nodeValue = "$namespace/$lastLeftOf"

                // set lastLeftOf attribute to the the current element
                val nameSpaceLength = 4
                lastLeftOf = element.attributes.getNamedItem("android:id").nodeValue.substring(nameSpaceLength)

                // copy the element
                destinationResourceFile.adoptNode(element)
                destinationElement.appendChild(element)
            }

            // TODO: by adding the ability to "finalize" a patch,
            //  this method would not have to open and close the resource files every time.
            sourceXmlEditor.close()
            targetXmlEditor.close()
        }
    }
}