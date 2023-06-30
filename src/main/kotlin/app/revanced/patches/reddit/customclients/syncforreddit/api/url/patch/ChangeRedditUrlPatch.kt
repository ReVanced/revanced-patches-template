package app.revanced.patches.reddit.customclients.syncforreddit.api.url.patch

import android.os.Environment
import app.revanced.extensions.resolveMany
import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.reddit.customclients.syncforreddit.api.url.fingerprints.GetRedditImageUrlFingerprint
import app.revanced.patches.reddit.customclients.syncforreddit.api.url.fingerprints.GetRedditUrlFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.StringReference
import java.io.File

@Patch
@Name("change-reddit-url")
@Description("Replaces the Reddit URL with one from another service.")
@Compatibility(
    [
        Package("com.laurencedawson.reddit_sync"),
        Package("com.laurencedawson.reddit_sync.pro"),
        Package("com.laurencedawson.reddit_sync.dev"),
    ]
)
@Version("1.0.0")
class ChangeRedditUrlPatch : BytecodePatch(
    listOf(GetRedditUrlFingerprint, GetRedditImageUrlFingerprint)
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

        GetRedditUrlFingerprint
            .resolveMany(context, context.classes)
            .replaceAll {
                val replacement = it.replace(
                    "^((?:https?://)?(?:[a-z0-9-]+\\.)*)?reddit\\.com(.*)$".toRegex(),
                    "$1$url$2"
                )
                val replaced = replacement != it

                print("$it: $replaced")
                if (replaced) print(": $replacement")
                println()

                ReplacementResults(
                    replacement,
                    replaced
                )
            }

        if (replacePreview == true) {
            GetRedditImageUrlFingerprint
                .resolveMany(context, context.classes)
                .replaceAll {
                    print("$it: ")
                    println(true)
                    ReplacementResults(
                        it.replace(
                            "preview.redd.it",
                            "images.$url"
                        ),
                        replaced = true
                    )
                }
        }

        return PatchResultSuccess()
    }

    data class ReplacementResults(val replacement: String, val replaced: Boolean)

    private fun Sequence<MethodFingerprintResult>.replaceAll(replace: (replacing: String) -> ReplacementResults) {
        this.forEach {
            it.mutableMethod.apply {
                it.scanResult.stringsScanResult!!.matches.forEach { match ->
                    val redditUrlStringInstruction = getInstruction<ReferenceInstruction>(match.index)
                    val targetRegister =
                        (redditUrlStringInstruction as OneRegisterInstruction).registerA
                    val reference = redditUrlStringInstruction.reference as StringReference

                    val newRedditUrl = replace(reference.string)

                    if (newRedditUrl.replaced) {
                        replaceInstruction(
                            match.index,
                            "const-string v$targetRegister, \"${newRedditUrl.replacement}\""
                        )
                    }
                }
            }
        }
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
        var replacePreview by option(
            PatchOption.BooleanOption(
                "replacePreview",
                false,
                "Replace preview.redd.it URLs as well",
                "Replaces preview.redd.it URLs with images.\$url."
            )
        )
    }
}
