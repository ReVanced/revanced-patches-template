package app.revanced.patches.youtube.misc.settings.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.DomFileEditor
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
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
        val classLoader = this.javaClass.classLoader

        appendToXML("values/arrays.xml", classLoader, data)
        appendToXML("values/strings.xml", classLoader, data)

        copyFile("xml/revanced_prefs.xml", classLoader, data)
        copyFile("layout/xsettings_toolbar.xml", classLoader, data)
        copyFile("layout/xsettings_with_toolbar.xml", classLoader, data)
        copyFile("layout/xsettings_with_toolbar_layout.xml", classLoader, data)

        /*
        Only non-root variant
        */
        appendSettingsActivity(data)

        return PatchResultSuccess()
    }

    private fun copyFile(file: String, classLoader: ClassLoader, data: ResourceData) {
        val resDirectory = data["res"]
        val prefsFile = this.javaClass.classLoader.getResourceAsStream("settings/$file")!!

        Files.copy(
            prefsFile,
            resDirectory.resolve(file).toPath()
        )
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

    private fun appendSettingsActivity(data: ResourceData) {
        data.xmlEditor["res/xml/settings_fragment.xml"].use {
            val rydSettingsElementIntent = it.file.createElement("intent")
            rydSettingsElementIntent.setAttribute("android:data", "ryd_settings")
            rydSettingsElementIntent.setAttribute("android:targetPackage", "com.revanced.android.youtube")
            rydSettingsElementIntent.setAttribute(
                "android:targetClass",
                "app.revanced.integrations.settingsmenu.ReVancedSettingActivity"
            )
            val rydSettingsElement = it.file.createElement("Preference")
            rydSettingsElement.setAttribute("android:title", "@string/revanced_ryd_settings_title")
            rydSettingsElement.setAttribute("android:summary", "@string/revanced_ryd_settings_summary")
            rydSettingsElement.appendChild(rydSettingsElementIntent)

            it.file.firstChild.appendChild(rydSettingsElement)

            val sbSettingsElementIntent = it.file.createElement("intent")
            sbSettingsElementIntent.setAttribute("android:data", "sponsorblock_settings")
            sbSettingsElementIntent.setAttribute("android:targetPackage", "com.revanced.android.youtube")
            sbSettingsElementIntent.setAttribute(
                "android:targetClass",
                "app.revanced.integrations.settingsmenu.ReVancedSettingActivity"
            )
            val sbSettingsElement = it.file.createElement("Preference")
            sbSettingsElement.setAttribute("android:title", "@string/sb_settings")
            sbSettingsElement.setAttribute("android:summary", "@string/sb_summary")
            sbSettingsElement.appendChild(sbSettingsElementIntent)

            it.file.firstChild.appendChild(sbSettingsElement)

            val rvSettingsElementIntent = it.file.createElement("intent")
            rvSettingsElementIntent.setAttribute("android:data", "revanced_settings")
            rvSettingsElementIntent.setAttribute("android:targetPackage", "com.revanced.android.youtube")
            rvSettingsElementIntent.setAttribute(
                "android:targetClass",
                "app.revanced.integrations.settingsmenu.ReVancedSettingActivity"
            )
            val rvSettingsElement = it.file.createElement("Preference")
            rvSettingsElement.setAttribute("android:title", "@string/revanced_settings")
            rvSettingsElement.appendChild(rvSettingsElementIntent)

            it.file.firstChild.appendChild(rvSettingsElement)
        }
    }
}
