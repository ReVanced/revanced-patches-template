package app.revanced.patches.youtube.layout.sponsorblock.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.layout.sponsorblock.annotations.SponsorBlockCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.Preference
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.layout.autocaptions.fingerprints.StartVideoInformerFingerprint
import app.revanced.patches.youtube.layout.sponsorblock.bytecode.fingerprints.ShortsPlayerConstructorFingerprint
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.Settings.mergeStrings
import app.revanced.util.resources.ResourceUtils.copyResources
import app.revanced.util.resources.ResourceUtils.copyXmlNode

@Name("sponsorblock-resource-patch")
@SponsorBlockCompatibility
@DependsOn([FixLocaleConfigErrorPatch::class, SettingsPatch::class])
@Version("0.0.1")
class SponsorBlockResourcePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        val youtubePackage = "com.google.android.youtube"
        SettingsPatch.addPreference(
            Preference(
                StringResource("sb_settings", "SponsorBlock"),
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
        data.mergeStrings("sponsorblock/host/values/strings.xml")

        /*
         merge SponsorBlock drawables to main drawables
         */

        arrayOf(
            ResourceUtils.ResourceGroup(
                "layout",
                "inline_sponsor_overlay.xml",
                "new_segment.xml",
                "skip_sponsor_button.xml"
            ),
            ResourceUtils.ResourceGroup(
                // required resource for back button, because when the base APK is used, this resource will not exist
                "drawable",
                "ic_sb_adjust.xml",
                "ic_sb_compare.xml",
                "ic_sb_edit.xml",
                "ic_sb_logo.xml",
                "ic_sb_publish.xml",
                "ic_sb_voting.xml"
            ),
            ResourceUtils.ResourceGroup(
                // required resource for back button, because when the base APK is used, this resource will not exist
                "drawable-xxxhdpi", "quantum_ic_skip_next_white_24.png"
            )
        ).forEach { resourceGroup ->
            data.copyResources("sponsorblock", resourceGroup)
        }

        /*
        merge xml nodes from the host to their real xml files
         */

        // copy nodes from host resources to their real xml files
        val hostingResourceStream =
            classLoader.getResourceAsStream("sponsorblock/host/layout/youtube_controls_layout.xml")!!

        val targetXmlEditor = data.xmlEditor["res/layout/youtube_controls_layout.xml"]
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

        val startVideoInformerMethod = StartVideoInformerFingerprint.result!!.mutableMethod

        startVideoInformerMethod.addInstructions(
            0, """
            const/4 v0, 0x1
            sput-boolean v0, Lapp/revanced/integrations/settings/SettingsEnum;->SB_SHORTS_ENABLED:Z
        """
        )

        val shortsPlayerConstructorMethod = ShortsPlayerConstructorFingerprint.result!!.mutableMethod

        shortsPlayerConstructorMethod.addInstructions(
            0, """
            const/4 v0, 0x0
            sput-boolean v0, Lapp/revanced/integrations/settings/SettingsEnum;->SB_SHORTS_ENABLED:Z
        """
        )

        return PatchResultSuccess()
    }
}