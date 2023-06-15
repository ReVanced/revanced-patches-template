package app.revanced.patches.boostforreddit.api.patch

import android.os.Environment
import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.boostforreddit.api.fingerprints.GetClientIdFingerprint
import java.io.File
@Patch
@Name("change-oauth-client-id")
@Description("Changes the OAuth client ID in Boost for Reddit..")
@Compatibility(
    [
        Package("com.rubenmayayo.reddit"),
    ]
)
@Version("0.0.1")
class ChangeOAuthClientIdPatch : BytecodePatch(
    listOf(GetClientIdFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        if (clientId == null) {
            // Test if on Android
            try {
                Class.forName("android.os.Environment")
            } catch (e: ClassNotFoundException) {
                return PatchResultError("No client ID provided")
            }

            File(Environment.getExternalStorageDirectory(), "reddit_client_id_revanced.txt").also {
                if (it.exists()) return@also

                val error = """
                    In order to use this patch, you need to provide a client ID.
                    You can do this by creating a file at ${it.absolutePath} with the client ID as its content.
                    Alternatively, you can provide the client ID using patch options.
                    
                    You can get your client ID from https://www.reddit.com/prefs/apps.
                    The application type has to be "installed app" and the redirect URI has to be set to "http://rubenmayayo.com""
                """.trimIndent()

                return PatchResultError(error)
            }.let { clientId = it.readText().trim() }
        }

        GetClientIdFingerprint.result?.also { result ->
                result.mutableMethod.addInstructions(
                        0,
                        """
                             const-string v0, "$clientId"
                             return-object v0
                        """
                )
        } ?: return PatchResultError("Could not find required method to patch.")

        return PatchResultSuccess()
    }

    companion object : OptionsContainer() {
        var clientId by option(
            PatchOption.StringOption(
                "client-id-boost",
                null,
                "OAuth client ID in Boost for Reddit.",
                "The client ID to use for OAuth."
            )
        )
    }
}
