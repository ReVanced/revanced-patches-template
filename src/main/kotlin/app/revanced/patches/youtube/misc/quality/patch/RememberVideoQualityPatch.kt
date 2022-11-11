package app.revanced.patches.youtube.misc.quality.patch

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
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.quality.annotations.DefaultVideoQualityCompatibility
import app.revanced.patches.youtube.misc.quality.fingerprints.VideoQualityReferenceFingerprint
import app.revanced.patches.youtube.misc.quality.fingerprints.VideoQualitySetterFingerprint
import app.revanced.patches.youtube.misc.quality.fingerprints.VideoUserQualityChangeFingerprint
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.ListPreference
import app.revanced.patches.youtube.misc.settings.framework.components.impl.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference
import app.revanced.patches.youtube.misc.videoid.patch.VideoIdPatch
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.FieldReference

@Patch
@DependsOn([IntegrationsPatch::class, VideoIdPatch::class, SettingsPatch::class])
@Name("remember-video-quality")
@Description("Adds the ability to remember the video quality you chose in the video quality flyout.")
@DefaultVideoQualityCompatibility
@Version("0.0.1")
class RememberVideoQualityPatch : BytecodePatch(
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
                        StringResource("revanced_default_video_quality_wifi_title", "Default video quality Wi-Fi"),
                        entries = ArrayResource("revanced_default_video_quality_wifi_entries", listOf(
                            StringResource("Auto","Auto"),
                            StringResource("144p","144p"),
                            StringResource("240p","240p"),
                            StringResource("360p","360p"),
                            StringResource("480p","480p"),
                            StringResource("720p","720p"),
                            StringResource("1080p","1080p"),
                            StringResource("1440p","1440p"),
                            StringResource("2160p","2160p")
                        )),
                        entryValues = ArrayResource("revanced_default_video_quality_wifi_entryValues", listOf(
                            StringResource("-2","-2"),
                            StringResource("144","144"),
                            StringResource("240","240"),
                            StringResource("360","360"),
                            StringResource("480","480"),
                            StringResource("720","720"),
                            StringResource("1080","1080"),
                            StringResource("1440","1440"),
                            StringResource("2160","2160")
                        )),
                        StringResource("revanced_default_video_quality_wifi_summary", "Select default video resolution on Wi-Fi Network")
                    ),
                    ListPreference(
                        "revanced_default_video_quality_mobile",
                        StringResource("revanced_default_video_quality_mobile_title", "Default video quality Cellular"),
                        entries = ArrayResource("revanced_default_video_quality_mobile_entries", listOf(
                            StringResource("Auto","Auto"),
                            StringResource("144p","144p"),
                            StringResource("240p","240p"),
                            StringResource("360p","360p"),
                            StringResource("480p","480p"),
                            StringResource("720p","720p"),
                            StringResource("1080p","1080p"),
                            StringResource("1440p","1440p"),
                            StringResource("2160p","2160p")
                        )),
                        entryValues = ArrayResource("revanced_default_video_quality_mobile_entryValues", listOf(
                            StringResource("-2","-2"),
                            StringResource("144","144"),
                            StringResource("240","240"),
                            StringResource("360","360"),
                            StringResource("480","480"),
                            StringResource("720","720"),
                            StringResource("1080","1080"),
                            StringResource("1440","1440"),
                            StringResource("2160","2160")
                        )),
                        StringResource("revanced_default_video_quality_mobile_summary", "Select default video resolution on Cellular Network")
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
