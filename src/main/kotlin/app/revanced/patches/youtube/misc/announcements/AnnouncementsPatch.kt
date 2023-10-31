package app.revanced.patches.youtube.misc.announcements

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.fingerprints.WatchWhileActivityFingerprint
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import com.android.tools.smali.dexlib2.Opcode

@Patch(
    name = "Announcements",
    description = "Shows ReVanced announcements on startup.",
    compatiblePackages = [CompatiblePackage("com.google.android.youtube")],
    dependencies = [SettingsPatch::class]
)
@Suppress("unused")
object AnnouncementsPatch : BytecodePatch(
    setOf(WatchWhileActivityFingerprint)
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/patches/announcements/AnnouncementsPatch;"

    override fun execute(context: BytecodeContext) {
        val onCreateMethod = WatchWhileActivityFingerprint.result?.let {
            it.mutableClass.methods.find { method -> method.name == "onCreate" }
        } ?: throw WatchWhileActivityFingerprint.exception

        val superCallIndex = onCreateMethod.getInstructions().indexOfFirst { it.opcode == Opcode.INVOKE_SUPER_RANGE }

        onCreateMethod.addInstructions(
            superCallIndex + 1,
            "invoke-static { v1 }, $INTEGRATIONS_CLASS_DESCRIPTOR->showAnnouncement(Landroid/app/Activity;)V"
        )

        SettingsPatch.includePatchStrings("Announcements")
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_announcements",
                "revanced_announcements_title",
                "revanced_announcements_summary_on",
                "revanced_announcements_summary_off",
            )
        )
    }
}
