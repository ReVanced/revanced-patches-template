package app.revanced.patches.youtube.misc.video.quality.patch

import app.revanced.extensions.toErrorResult
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
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.video.quality.annotations.DefaultVideoQualityCompatibility
import app.revanced.patches.youtube.misc.video.quality.fingerprints.VideoQualityReferenceFingerprint
import app.revanced.patches.youtube.misc.video.quality.fingerprints.VideoQualitySetterFingerprint
import app.revanced.patches.youtube.misc.video.quality.fingerprints.VideoUserQualityChangeFingerprint
import app.revanced.patches.youtube.misc.video.videoid.patch.VideoIdPatch
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.FieldReference

@Patch
@DependsOn([IntegrationsPatch::class, VideoIdPatch::class, SettingsPatch::class])
@Name("default-video-quality")
@Description("Adds the ability to remember the video quality.")
@DefaultVideoQualityCompatibility
@Version("0.0.1")
class DefaultVideoQualityPatch : BytecodePatch(
    listOf(
        VideoQualitySetterFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val (keys, values) = buildMap {
            fun add(quality: String, entry: String, entryValue: String) {
                fun String.withValue(value: String) = StringResource(this, value)

                put(
                    "revanced_video_quality_${quality}_entry".withValue(entry),
                    "revanced_video_quality_${quality}_entry_value".withValue(entryValue)
                )
            }

            add("auto", "Auto", "-2")

            arrayOf("144", "240", "360", "480", "720", "1080", "1440", "2160").forEach {
                add(it, "${it}p", it)
            }
        }.let { map ->
            map.keys.toList() to map.values.toList()
        }

        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            PreferenceScreen(
                "revanced_default_video_quality",
                StringResource("revanced_default_video_quality_title", "Video quality settings"),
                listOf(
                    SwitchPreference(
                        "revanced_remember_video_quality_selection",
                        StringResource(
                            "revanced_remember_video_quality_selection_title",
                            "Remember current video quality"
                        ),
                        true,
                        StringResource(
                            "revanced_remember_video_quality_selection_summary_on",
                            "The current video quality will not change"
                        ),
                        StringResource(
                            "revanced_remember_video_quality_selection_summary_off",
                            "Video quality will be remembered until a new quality is chosen"
                        )
                    ),
                    ListPreference(
                        "revanced_default_video_quality_wifi",
                        StringResource(
                            "revanced_default_video_quality_wifi_title",
                            "Change video quality on Wi-Fi network"
                        ),
                        ArrayResource("revanced_video_quality_wifi_entries", keys),
                        ArrayResource("revanced_video_quality_wifi_entry_values", values),
                    ),
                    ListPreference(
                        "revanced_default_video_quality_mobile",
                        StringResource(
                            "revanced_default_video_quality_mobile_title",
                            "Change video quality on mobile network"
                        ),
                        ArrayResource("revanced_video_quality_mobile_entries", keys),
                        ArrayResource("revanced_video_quality_mobile_entry_values", values)
                    )
                ),
                StringResource("revanced_default_video_quality_summary", "Select default video quality")
            )
        )


        val setterMethod = VideoQualitySetterFingerprint.result ?: return VideoQualitySetterFingerprint.toErrorResult()

        if (!VideoUserQualityChangeFingerprint.resolve(context, setterMethod.classDef))
            return VideoUserQualityChangeFingerprint.toErrorResult()

        val userQualityMethod = VideoUserQualityChangeFingerprint.result!!

        if (!VideoQualityReferenceFingerprint.resolve(context, setterMethod.classDef))
            return VideoQualityReferenceFingerprint.toErrorResult()

        val qualityFieldReference =
            VideoQualityReferenceFingerprint.result!!.method.let { method ->
                (method.implementation!!.instructions.elementAt(0) as ReferenceInstruction).reference as FieldReference
            }

        VideoIdPatch.injectCall("Lapp/revanced/integrations/patches/playback/quality/DefaultVideoQualityPatch;->newVideoStarted(Ljava/lang/String;)V")

        val qIndexMethodName =
            context.classes.single { it.type == qualityFieldReference.type }.methods.single { it.parameterTypes.first() == "I" }.name

        setterMethod.mutableMethod.addInstructions(
            0,
            """
                iget-object v0, p0, ${setterMethod.classDef.type}->${qualityFieldReference.name}:${qualityFieldReference.type}
                const-string v1, "$qIndexMethodName"
		        invoke-static {p1, p2, v0, v1}, Lapp/revanced/integrations/patches/playback/quality/DefaultVideoQualityPatch;->setVideoQuality([Ljava/lang/Object;ILjava/lang/Object;Ljava/lang/String;)I
   		        move-result p2
            """,
        )

        userQualityMethod.mutableMethod.addInstruction(
            0,
            "invoke-static {p3}, Lapp/revanced/integrations/patches/playback/quality/DefaultVideoQualityPatch;->userChangedQuality(I)V"
        )

        return PatchResultSuccess()
    }
}
