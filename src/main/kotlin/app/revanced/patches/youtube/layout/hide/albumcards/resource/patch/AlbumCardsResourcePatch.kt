package app.revanced.patches.youtube.layout.hide.albumcards.resource.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.albumcards.annotations.AlbumCardsCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.resourceIdOf

@Name("hide-album-cards-resource-patch")
@AlbumCardsCompatibility
@DependsOn([SettingsPatch::class])
@Version("0.0.1")
class AlbumCardsResourcePatch : ResourcePatch {
    companion object {
        internal var albumCardId: Long = -1
    }

    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_album_cards",
                StringResource("revanced_hide_album_cards_title", "Hide album cards"),
                false,
                StringResource("revanced_hide_album_cards_summary_on", "Album cards are hidden"),
                StringResource("revanced_hide_album_cards_summary_off", "Album cards are shown")
            )
        )

        albumCardId = context.resourceIdOf("layout", "album_card")

    }
}