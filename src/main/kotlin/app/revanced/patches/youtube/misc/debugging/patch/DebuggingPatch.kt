package app.revanced.patches.youtube.misc.debugging.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.debugging.annotations.DebuggingCompatibility
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@Name("Enable debugging")
@DependsOn([IntegrationsPatch::class, YouTubeSettingsPatch::class])
@Description("Adds debugging options.")
@DebuggingCompatibility
class DebuggingPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.MISC.addPreferences(
            app.revanced.patches.shared.settings.preference.impl.PreferenceScreen(
                "revanced_debug_preference_screen",
                "revanced_debug_preference_screen_title",
                listOf(
                    SwitchPreference(
                        "revanced_debug",
                        "revanced_debug_title",
                        "revanced_debug_summary_on",
                        "revanced_debug_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_debug_protobuffer",
                        StringResource(
                            "revanced_debug_protobuffer_title",
                            "Log protocol buffer"
                        ),
                        StringResource("revanced_debug_protobuffer_summary_on", "Debug logs include proto buffer"),
                        StringResource("revanced_debug_protobuffer_summary_off", "Debug logs do not include proto buffer")
                    ),
                    SwitchPreference(
                        "revanced_debug_stacktrace",
                        "revanced_debug_stacktrace_title",
                        "revanced_debug_stacktrace_summary_on",
                        "revanced_debug_stacktrace_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_debug_toast_on_error",
                        "revanced_debug_toast_on_error_title",
                        "revanced_debug_toast_on_error_summary_on",
                        "revanced_debug_toast_on_error_summary_off"
                    ),
                ),
                "revanced_debug_preference_screen_summary"
            )
        )

        return PatchResultSuccess()
    }
}
