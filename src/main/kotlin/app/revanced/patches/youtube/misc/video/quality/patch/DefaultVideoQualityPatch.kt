package app.revanced.patches.youtube.misc.video.quality.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.*
import app.revanced.patches.shared.settings.preference.impl.ArrayResource
import app.revanced.patches.shared.settings.preference.impl.ListPreference
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.video.quality.fingerprints.VideoQualityReferenceFingerprint
import app.revanced.patches.youtube.misc.video.quality.fingerprints.VideoQualitySetterFingerprint
import app.revanced.patches.youtube.misc.video.quality.fingerprints.VideoUserQualityChangeFingerprint
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.video.quality.annotations.DefaultVideoQualityCompatibility
import app.revanced.patches.youtube.misc.video.videoid.patch.VideoIdPatch
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.FieldReference

@Patch
@DependsOn([IntegrationsPatch::class, VideoIdPatch::class, SettingsPatch::class])
@Name("default-video-quality")
@Description("Adds the ability to remember the video quality you chose in the revanced video quality settings.")
@DefaultVideoQualityCompatibility
@Version("0.0.1")
class DefaultVideoQualityPatch : BytecodePatch(
    listOf(
        VideoQualitySetterFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            PreferenceScreen(
                "revanced_default_video_quality",
                StringResource("revanced_default_video_quality_title", "Video quality settings"),
                listOf(
                    SwitchPreference(
                        "revanced_remember_video_quality_selection",
                        StringResource("revanced_remember_video_quality_selection_title", "Remember current video quality"),
                        true,
                        StringResource("revanced_remember_video_quality_selection_summary_on", "The current video quality will not change"),
                        StringResource("revanced_remember_video_quality_selection_summary_off", "Video quality will be remembered until a new quality is chosen")
                    ),
                    ListPreference(
                        "revanced_default_video_quality_wifi",
                        StringResource("revanced_default_video_quality_wifi_title", "Default video quality on Wi-Fi network"),
                        ArrayResource(
                            "revanced_video_quality_wifi_entries",
                            listOf(
                                StringResource("revanced_video_quality_auto_entry", "Auto"),
                                StringResource("revanced_video_quality_144_entry", "144p"),
                                StringResource("revanced_video_quality_240_entry", "240p"),
                                StringResource("revanced_video_quality_360_entry", "360p"),
                                StringResource("revanced_video_quality_480_entry", "480p"),
                                StringResource("revanced_video_quality_720_entry", "720p"),
                                StringResource("revanced_video_quality_1080_entry", "1080p"),
                                StringResource("revanced_video_quality_1440_entry", "1440p"),
                                StringResource("revanced_video_quality_2160_entry", "2160p")
                            )
                        ),
                        ArrayResource(
                            "revanced_video_quality_wifi_entryValues",
                            listOf(
                                StringResource("revanced_video_quality_auto_entryValue", "-2"),
                                StringResource("revanced_video_quality_144_entryValue", "144"),
                                StringResource("revanced_video_quality_240_entryValue", "240"),
                                StringResource("revanced_video_quality_360_entryValue", "360"),
                                StringResource("revanced_video_quality_480_entryValue", "480"),
                                StringResource("revanced_video_quality_720_entryValue", "720"),
                                StringResource("revanced_video_quality_1080_entryValue", "1080"),
                                StringResource("revanced_video_quality_1440_entryValue", "1440"),
                                StringResource("revanced_video_quality_2160_entryValue", "2160")
                            )
                        )
                    ),
                    ListPreference(
                        "revanced_default_video_quality_mobile",
                        StringResource("revanced_default_video_quality_mobile_title", "Default video quality on Mobile network"),
                        ArrayResource(
                            "revanced_video_quality_mobile_entries",
                            listOf(
                                StringResource("revanced_video_quality_auto_entry", "Auto"),
                                StringResource("revanced_video_quality_144_entry", "144p"),
                                StringResource("revanced_video_quality_240_entry", "240p"),
                                StringResource("revanced_video_quality_360_entry", "360p"),
                                StringResource("revanced_video_quality_480_entry", "480p"),
                                StringResource("revanced_video_quality_720_entry", "720p"),
                                StringResource("revanced_video_quality_1080_entry", "1080p"),
                                StringResource("revanced_video_quality_1440_entry", "1440p"),
                                StringResource("revanced_video_quality_2160_entry", "2160p")
                            )
                        ),
                        ArrayResource(
                            "revanced_video_quality_mobile_entryValues",
                            listOf(
                                StringResource("revanced_video_quality_auto_entryValue", "-2"),
                                StringResource("revanced_video_quality_144_entryValue", "144"),
                                StringResource("revanced_video_quality_240_entryValue", "240"),
                                StringResource("revanced_video_quality_360_entryValue", "360"),
                                StringResource("revanced_video_quality_480_entryValue", "480"),
                                StringResource("revanced_video_quality_720_entryValue", "720"),
                                StringResource("revanced_video_quality_1080_entryValue", "1080"),
                                StringResource("revanced_video_quality_1440_entryValue", "1440"),
                                StringResource("revanced_video_quality_2160_entryValue", "2160")
                            )
                        )
                    )
                ),
                StringResource("revanced_default_video_quality_summary", "Select default video quality")
            )
        )


        val setterMethod = VideoQualitySetterFingerprint.result!!

        VideoUserQualityChangeFingerprint.resolve(context, setterMethod.classDef)
        val userQualityMethod = VideoUserQualityChangeFingerprint.result!!

        VideoQualityReferenceFingerprint.resolve(context, setterMethod.classDef)
        val qualityFieldReference =
            VideoQualityReferenceFingerprint.result!!.method.let { method ->
                (method.implementation!!.instructions.elementAt(0) as ReferenceInstruction).reference as FieldReference
            }

        VideoIdPatch.injectCall("Lapp/revanced/integrations/patches/playback/quality/RememberVideoQualityPatch;->newVideoStarted(Ljava/lang/String;)V")

        val qIndexMethodName =
            context.classes.single { it.type == qualityFieldReference.type }.methods.single { it.parameterTypes.first() == "I" }.name

        setterMethod.mutableMethod.addInstructions(
            0,
            """
                iget-object v0, p0, ${setterMethod.classDef.type}->${qualityFieldReference.name}:${qualityFieldReference.type}
                const-string v1, "$qIndexMethodName"
		        invoke-static {p1, p2, v0, v1}, Lapp/revanced/integrations/patches/playback/quality/RememberVideoQualityPatch;->setVideoQuality([Ljava/lang/Object;ILjava/lang/Object;Ljava/lang/String;)I
   		        move-result p2
            """,
        )

        userQualityMethod.mutableMethod.addInstruction(
            0,
            "invoke-static {p3}, Lapp/revanced/integrations/patches/playback/quality/RememberVideoQualityPatch;->userChangedQuality(I)V"
        )

        return PatchResultSuccess()
    }
}
