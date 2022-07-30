package app.revanced.patches.youtube.mist.settings.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
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

        val iconFile = this.javaClass.classLoader.getResourceAsStream("res/xml/revanced_prefs.xml")
            ?: return PatchResultError("The file revanced_prefs.xml can not be found.")

        /*Files.write(
            resDirectory.resolve("mipmap-$iconDirectory").resolve("$iconName.png").toPath(), iconFile.readAllBytes()
        )*/

        return PatchResultSuccess()
    }
}
