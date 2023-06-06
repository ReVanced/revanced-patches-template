package app.revanced.patches.youtube.layout.hide.artistcards.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.artistcards.annotations.HideArtistCardCompatibility
import app.revanced.patches.youtube.misc.litho.filter.patch.LithoFilterPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@DependsOn([ResourceMappingPatch::class, LithoFilterPatch::class])
@Name("hide-artist-card")
@Description("Hides the artist card below the searchbar.")
@HideArtistCardCompatibility
@Version("0.0.1")
class HideArtistCardsPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_artist_cards",
                "revanced_hide_artist_cards_title",
                "revanced_hide_artist_cards_on",
                "revanced_hide_artist_cards_off"
            ),
        )
        return PatchResultSuccess()
    }
}
