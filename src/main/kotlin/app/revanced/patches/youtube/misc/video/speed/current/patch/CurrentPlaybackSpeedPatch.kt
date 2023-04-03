package app.revanced.patches.youtube.misc.video.speed.current.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.video.speed.current.annotation.CurrentPlaybackSpeedCompatibility
import app.revanced.patches.youtube.misc.video.speed.current.fingerprint.OnPlaybackSpeedItemClickFingerprint
import app.revanced.patches.youtube.misc.video.videoid.patch.VideoIdPatch
import org.jf.dexlib2.iface.instruction.FiveRegisterInstruction

@Name("current-playback-speed")
@Description("Hook to get the current video playback speed")
@DependsOn([IntegrationsPatch::class, SettingsPatch::class, VideoIdPatch::class])
@CurrentPlaybackSpeedCompatibility
@Version("0.0.1")
class CurrentPlaybackSpeedPatch : BytecodePatch(
    listOf(
        OnPlaybackSpeedItemClickFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        VideoIdPatch.injectCall("$INTEGRATIONS_CLASS_DESCRIPTOR->newVideoLoaded(Ljava/lang/String;)V")

        // User selected a playback speed
        OnPlaybackSpeedItemClickFingerprint.result?.apply {
            setPlaybackSpeedIndex = scanResult.patternScanResult!!.startIndex - 3
            selectedPlaybackSpeedRegister =
                (mutableMethod.instruction(setPlaybackSpeedIndex) as FiveRegisterInstruction).registerD
            insertMethod = mutableMethod
        } ?: return OnPlaybackSpeedItemClickFingerprint.toErrorResult()

        injectVideoSpeedSelectedByUser("$INTEGRATIONS_CLASS_DESCRIPTOR->userSelectedPlaybackSpeed(F)V")

        return PatchResultSuccess()
    }

    companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/playback/speed/CurrentPlaybackSpeedPatch;"

        private var selectedPlaybackSpeedRegister = 0
        private var setPlaybackSpeedIndex = 0
        private lateinit var insertMethod: MutableMethod

        fun injectVideoSpeedSelectedByUser(
            methodDescriptor: String
        ) = insertMethod.addInstruction(
            setPlaybackSpeedIndex++,
            "invoke-static {v$selectedPlaybackSpeedRegister}, $methodDescriptor"
        )
    }
}