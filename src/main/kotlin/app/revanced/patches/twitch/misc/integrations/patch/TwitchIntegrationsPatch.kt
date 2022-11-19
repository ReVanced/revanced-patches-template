package app.revanced.patches.twitch.misc.integrations.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.twitch.misc.integrations.fingerprints.InitFingerprint
import app.revanced.patches.twitch.misc.integrations.annotations.TwitchIntegrationsCompatibility

@Patch
@Name("twitch-integrations")
@Description("Applies mandatory patches to implement the ReVanced integrations into the application.")
@TwitchIntegrationsCompatibility
@Version("0.0.1")
class TwitchIntegrationsPatch : BytecodePatch(
    listOf(
        InitFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        if (context.findClass("Lapp/revanced/twitch/utils/ReVancedUtils") == null)
            return PatchResultError("Integrations have not been merged yet. This patch can not succeed without the integrations.")

        with(InitFingerprint.result!!.mutableMethod) {
            val count = implementation!!.registerCount - 1
            addInstruction(
                0, "sput-object v$count, Lapp/revanced/twitch/utils/ReVancedUtils;->context:Landroid/content/Context;"
            )
        }

        return PatchResultSuccess()
    }
}