package app.revanced.patches.youtube.interaction.swipecontrols.patch.resource

import app.revanced.extensions.injectResources
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.interaction.swipecontrols.annotation.SwipeControlsCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Name("swipe-controls-resource-patch")
@SwipeControlsCompatibility
@Version("0.0.1")
class SwipeControlsResourcePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        SettingsPatch.PreferenceScreen.INTERACTIONS.addPreferences(
            PreferenceScreen(
                "revanced_swipe_controls", StringResource("revanced_swipe_controls_title", "Swipe controls"), listOf(
                    SwitchPreference(
                        "revanced_enable_swipe_brightness",
                        StringResource("revanced_swipe_brightness_enabled_title", "Enable brightness gesture"),
                        true,
                        StringResource("revanced_swipe_brightness_summary_on", "Brightness swipe is enabled."),
                        StringResource("revanced_swipe_brightness_summary_off", "Brightness swipe is disabled.")
                    ),
                    SwitchPreference(
                        "revanced_enable_swipe_volume",
                        StringResource("revanced_swipe_volume_enabled_title", "Enable volume gesture"),
                        true,
                        StringResource("revanced_swipe_volume_summary_on", "Volume swipe is enabled."),
                        StringResource("revanced_swipe_volume_summary_off", "Volume swipe is disabled.")
                    ),
                    SwitchPreference(
                        "revanced_enable_press_to_swipe",
                        StringResource("revanced_press_to_swipe_enabled_title", "Enable press-to-swipe gesture"),
                        false,
                        StringResource("revanced_press_to_swipe_summary_on", "Press-to-swipe is enabled."),
                        StringResource("revanced_press_to_swipe_summary_off", "Press-to-swipe is disabled.")
                    ),
                    SwitchPreference(
                        "revanced_enable_swipe_haptic_feedback",
                        StringResource("revanced_swipe_haptic_feedback_enabled_title", "Enable haptic feedback"),
                        true,
                        StringResource("revanced_swipe_haptic_feedback_summary_on", "Haptic feedback is enabled."),
                        StringResource("revanced_swipe_haptic_feedback_summary_off", "Haptic feedback is disabled.")
                    )

                    //TODO: add remaining components to swipe controls settings once they are implemented
                )
            )
        )

        val resourcesDir = "swipecontrols"

        data.injectResources(
            this.javaClass.classLoader,
            resourcesDir,
            "drawable",
            listOf(
                "ic_sc_brightness_auto",
                "ic_sc_brightness_manual",
                "ic_sc_volume_mute",
                "ic_sc_volume_normal"
            ).map { "$it.xml" }
        )
        return PatchResultSuccess()
    }
}
