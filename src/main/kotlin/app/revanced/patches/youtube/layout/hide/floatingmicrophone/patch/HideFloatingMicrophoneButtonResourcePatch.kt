package app.revanced.patches.youtube.layout.hide.floatingmicrophone.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.floatingmicrophone.annotations.HideFloatingMicrophoneButtonCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsResourcePatch

@DependsOn([SettingsPatch::class, ResourceMappingPatch::class])
@HideFloatingMicrophoneButtonCompatibility
class HideFloatingMicrophoneButtonResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_floating_microphone_button",
                "revanced_hide_floating_microphone_button_enabled_title",
                "revanced_hide_floating_microphone_button_summary_on",
                "revanced_hide_floating_microphone_button_summary_off"
            )
        )
        SettingsResourcePatch.mergePatchStrings("HideFloatingMicrophoneButton")

        fabButtonId = ResourceMappingPatch.resourceMappings.find { it.type == "id" && it.name == "fab" }?.id
            ?: throw PatchException("Can not find required fab button resource id")
    }

    internal companion object {
        var fabButtonId: Long = -1
    }
}
