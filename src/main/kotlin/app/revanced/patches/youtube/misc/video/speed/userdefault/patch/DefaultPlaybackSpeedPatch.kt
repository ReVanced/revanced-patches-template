package app.revanced.patches.youtube.misc.video.speed.userdefault.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.settings.preference.impl.ArrayResource
import app.revanced.patches.shared.settings.preference.impl.ListPreference
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.video.information.patch.VideoInformationPatch
import app.revanced.patches.youtube.misc.video.information.patch.VideoInformationPatch.Companion.reference
import app.revanced.patches.youtube.misc.video.speed.custom.patch.CustomVideoSpeedPatch
import app.revanced.patches.youtube.misc.video.speed.userdefault.annotation.DefaultPlaybackSpeedCompatibility
import app.revanced.patches.youtube.misc.video.speed.userdefault.fingerprint.InitializePlaybackSpeedValuesFingerprint
import app.revanced.patches.youtube.misc.video.videoid.patch.VideoIdPatch

@Patch
@Name("default-playback-speed")
@Description("Adds the option to set a default video playback speed.")
@DependsOn(
    [
        IntegrationsPatch::class,
        SettingsPatch::class,
        VideoIdPatch::class,
        VideoInformationPatch::class,
        CustomVideoSpeedPatch::class
    ]
)
@DefaultPlaybackSpeedCompatibility
@Version("0.0.1")
class DefaultPlaybackSpeedPatch : BytecodePatch(
    listOf(
        InitializePlaybackSpeedValuesFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_remember_playback_speed_last_selected",
                StringResource(
                    "revanced_remember_playback_speed_last_selected_title",
                    "Remember playback speed changes"
                ),
                true,
                StringResource(
                    "revanced_remember_playback_speed_last_selected_summary_on",
                    "Playback speed changes apply to all videos"
                ),
                StringResource(
                    "revanced_remember_playback_speed_last_selected_summary_off",
                    "Playback speed changes only apply to the current video"
                )
            )
        )

        // use same entry and values as custom video speed
        val videoSpeedsAvailable = CustomVideoSpeedPatch.videoSpeedsAvailable
        val entries = ArrayList<StringResource>(videoSpeedsAvailable.size)
        val entryValues = ArrayList<StringResource>(videoSpeedsAvailable.size)
        var index = 0
        videoSpeedsAvailable.forEach { number ->
            val numberString = number.toString()
            entries.add(StringResource("revanced_default_playback_speed_entry_$index", numberString + "x"))
            entryValues.add(StringResource("revanced_default_playback_speed_entry_value_$index", numberString))
            index++
        }
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            ListPreference(
                "revanced_default_playback_speed",
                StringResource(
                    "revanced_default_playback_speed_title",
                    "Default playback speed"
                ),
                ArrayResource("revanced_default_playback_speed_entries", entries),
                ArrayResource("revanced_default_playback_speed_entry_values", entryValues)
                // default value and summary are set by integrations after loading
            )
        )

        VideoIdPatch.injectCall("${INTEGRATIONS_CLASS_DESCRIPTOR}->newVideoLoaded(Ljava/lang/String;)V")

        VideoInformationPatch.userSelectedPlaybackSpeedHook(
            INTEGRATIONS_CLASS_DESCRIPTOR, "userSelectedPlaybackSpeed")

        /*
         * Hook the code that is called when the playback speeds are initialized, and sets the playback speed
         */
        InitializePlaybackSpeedValuesFingerprint.result?.apply {
            // Infer everything necessary for calling the method setPlaybackSpeed().
            val onItemClickListenerClassFieldReference = mutableMethod.instruction(0).reference

            // Registers are not used at index 0, so they can be freely used.
            mutableMethod.addInstructions(
                0,
                """
                    invoke-static { }, $INTEGRATIONS_CLASS_DESCRIPTOR->getPlaybackSpeedOverride()F
                    move-result v0
                    
                    # Check if the playback speed is not 1.0x.
                    const/high16 v1, 0x3f800000  # 1.0f
                    cmpg-float v1, v0, v1
                    if-eqz v1, :do_not_override
    
                    # Get the instance of the class which has the container class field below.
                    iget-object v1, p0, $onItemClickListenerClassFieldReference

                    # Get the container class field.
                    iget-object v1, v1, ${VideoInformationPatch.setPlaybackSpeedContainerClassFieldReference}  
                    
                    # Get the field from its class.
                    iget-object v2, v1, ${VideoInformationPatch.setPlaybackSpeedClassFieldReference}
                    
                    # Invoke setPlaybackSpeed on that class.
                    invoke-virtual {v2, v0}, ${VideoInformationPatch.setPlaybackSpeedMethodReference}
                """.trimIndent(),
                listOf(ExternalLabel("do_not_override", mutableMethod.instruction(0)))
            )
        } ?: return InitializePlaybackSpeedValuesFingerprint.toErrorResult()


        return PatchResultSuccess()
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/playback/speed/DefaultPlaybackSpeedPatch;"
    }
}