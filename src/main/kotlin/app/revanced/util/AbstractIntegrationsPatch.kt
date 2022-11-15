package app.revanced.util

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import org.jf.dexlib2.iface.Method

private val MISSING_INTEGRATIONS = PatchResultError(
    "Integrations have not been merged yet. " +
            "This patch can not succeed without merging the integrations."
)

@Description("Applies mandatory patches to implement the ReVanced integrations into the application.")
@Version("0.0.1")
abstract class AbstractIntegrationsPatch(
    private val fingerprints: Iterable<MethodFingerprint>,
    private val descriptor: String,
    private val register: (Method, MethodFingerprint) -> Int,
) : BytecodePatch(fingerprints) {
    override fun execute(context: BytecodeContext): PatchResult {
        if (context.findClass(descriptor) == null) return MISSING_INTEGRATIONS
        for (fingerprint in fingerprints) {
            with(fingerprint.result!!) {
                val register = register(method, fingerprint)
                mutableMethod.addInstruction(
                    0,
                    "sput-object v$register, $descriptor->context:Landroid/content/Context;"
                )
            }
        }
        return PatchResultSuccess()
    }
}