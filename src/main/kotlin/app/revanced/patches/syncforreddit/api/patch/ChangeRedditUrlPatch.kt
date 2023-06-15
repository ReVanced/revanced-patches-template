package app.revanced.patches.syncforreddit.api.patch

import android.os.Environment
import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.syncforreddit.api.fingerprints.GetRedditUrlFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.StringReference
import java.io.File
import java.util.*

@Patch
@Name("change-reddit-url")
@Description("Replaces the Reddit URL with one from another service.")
@Compatibility(
    [
        Package("com.laurencedawson.reddit_sync"),
        Package("com.laurencedawson.reddit_sync.pro"),
        Package("com.laurencedawson.reddit_sync.dev")
    ]
)
@Version("0.0.1")
class ChangeRedditUrlPatch : BytecodePatch(
    listOf(GetRedditUrlFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        if (url == null) {
            // Test if on Android
            try {
                Class.forName("android.os.Environment")
            } catch (e: ClassNotFoundException) {
                return PatchResultError("No url provided")
            }

            File(Environment.getExternalStorageDirectory(), "reddit_url_revanced.txt").also {
                if (it.exists()) return@also

                val error = """
                    In order to use this patch, you need to provide a URL.
                    You can do this by creating a file at ${it.absolutePath} with the URL as its content.
                    Alternatively, you can provide the URL using patch options.
                """.trimIndent()

                return PatchResultError(error)
            }.let { url = it.readText().trim() }
        }


        GetRedditUrlFingerprint.result?.let { fingerprint ->
            fingerprint.scanResult.stringsScanResult!!.matches.forEach { 
                val occurrenceIndex = it.index

                fingerprint.mutableMethod.apply {
                    val redditUrlStringInstruction = getInstruction<ReferenceInstruction>(occurrenceIndex)
                    val targetRegister = (redditUrlStringInstruction as OneRegisterInstruction).registerA
                    val reference = redditUrlStringInstruction.reference as StringReference

                    val newRedditUrl = reference.string.replace(
                        "reddit.com",
                        url!!
                    )

                    replaceInstruction(
                        occurrenceIndex,
                        "const-string v$targetRegister, \"$newRedditUrl\""
                    )
                }
            }
        } ?: return PatchResultError("Could not find required method to patch.")
        return PatchResultSuccess()
    }

    companion object : OptionsContainer() {
        var url by option(
            PatchOption.StringOption(
                "url",
                null,
                "Reddit Replacement URL",
                "The URL to replace Reddit with."
            )
        )
    }
}
