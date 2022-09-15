package app.revanced.patches.youtube.layout.theme.patch.themes

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.PatchDeprecated
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.layout.theme.annotations.ThemeCompatibility
import app.revanced.patches.youtube.layout.theme.patch.ThemePatch
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch

@Patch
@DependsOn([FixLocaleConfigErrorPatch::class])
@Name("materialyou")
@Description("Enables Material You colors. Works only for A12 and above.")
@ThemeCompatibility
@Version("0.0.1")
@PatchDeprecated("Theme patch already includes the Material You theme.", ThemePatch::class)
class MaterialYouPatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        ThemePatch.theme = ThemePatch.Themes.MaterialYou.name
        return ThemePatch().execute(data)
    }
}
