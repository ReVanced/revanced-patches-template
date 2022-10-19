package app.revanced.patches.youtube.layout.hidemymix.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.layout.hidemymix.annotations.MyMixCompatibility
import app.revanced.patches.youtube.layout.hidemymix.fingerprints.MyMixFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class])
@Name("hide-my-mix")
@Description("Remove My Mix from home feed.")
@MyMixCompatibility
@Version("0.0.1")
class MyMixPatch : BytecodePatch(
    listOf(
        MyMixFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_my_mix",
                StringResource("revanced_my_mix_title", "My Mix (Playlist)"),
                true,
                StringResource("revanced_my_mix_summary_on", "My Mix (Playlist) is hidden"),
                StringResource("revanced_my_mix_summary_off", "My Mix (Playlist) is shown")
            )
        )

        val result = MyMixFingerprint.result!!
        val method = result.mutableMethod
        val index = result.scanResult.patternScanResult!!.endIndex - 6
        val register = (method.implementation!!.instructions[index] as OneRegisterInstruction).registerA

        method.addInstruction(
            index + 2,
            "invoke-static {v$register}, Lapp/revanced/integrations/patches/HideMyMixPatch;->HideMyMix(Landroid/view/View;)V"
        )

        return PatchResultSuccess()
    }
}
