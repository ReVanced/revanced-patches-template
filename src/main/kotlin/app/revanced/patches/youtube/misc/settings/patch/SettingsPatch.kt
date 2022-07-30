package app.revanced.patches.youtube.misc.settings.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.DomFileEditor
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.layout.branding.icon.annotations.CustomBrandingCompatibility
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import java.io.OutputStream
import java.nio.file.Files

@Patch
@Dependencies([FixLocaleConfigErrorPatch::class, IntegrationsPatch::class])
@Name("settings")
@Description("Implements the Settings into ReVanced")
@CustomBrandingCompatibility
@Version("0.0.1")
class SettingsPatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        val resDirectory = data["res"]
        if (!resDirectory.isDirectory) return PatchResultError("The res folder can not be found.")

        val classLoader = this.javaClass.classLoader

        appendToXML("values/arrays.xml", classLoader, data)
        appendToXML("values/strings.xml", classLoader, data)

        val prefsPath = "xml/revanced_prefs.xml"
        val prefsFile = this.javaClass.classLoader.getResourceAsStream("settings/$prefsPath")!!

        Files.copy(
            prefsFile,
            resDirectory.resolve(prefsPath).toPath()
        )

        return PatchResultSuccess()
    }

    private fun appendToXML(file: String, classLoader: ClassLoader, data: ResourceData) {
        val inputStream = classLoader.getResourceAsStream("settings/$file")!!
        "resources".copyXmlNode(
            data.xmlEditor[inputStream, OutputStream.nullOutputStream()],
            data.xmlEditor["res/$file"]
        ).close() // close afterwards
    }

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
