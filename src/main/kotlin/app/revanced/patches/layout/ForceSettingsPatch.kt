package app.revanced.patches.layout

import app.revanced.patcher.PatcherData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.or
import app.revanced.patcher.patch.*
import app.revanced.patcher.signature.MethodMetadata
import app.revanced.patcher.signature.MethodSignature
import app.revanced.patcher.signature.MethodSignatureMetadata
import app.revanced.patcher.signature.PatternScanMethod
import app.revanced.patcher.smali.toInstructions
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

private val compatiblePackages = listOf(
    PackageMetadata(
        "com.google.android.youtube",
        listOf("17.14.35")
    )
)

class ForceSettingsPatch : Patch(
    PatchMetadata(
        "force-settings-patch",
        "Force Download and Background Playback Settings Patch",
        "Enable Download and Background Playback within Settings.",
        compatiblePackages,
        "0.0.1"
    ),
    listOf(
        MethodSignature(
            MethodSignatureMetadata(
                "force-settings-patch-manager",
                MethodMetadata(null, null), // unknown
                PatternScanMethod.Fuzzy(0), // FIXME: Test this threshold and find the best value.
                compatiblePackages,
                "Signature for the method required to be patched.",
                "0.0.1"
            ),
            "Z",
            AccessFlags.PUBLIC or AccessFlags.STATIC,
            listOf("L", "L"),
            listOf(
                Opcode.IF_EQZ,
                Opcode.INVOKE_INTERFACE,
                Opcode.MOVE_RESULT_OBJECT,
                Opcode.INVOKE_INTERFACE,
                Opcode.MOVE_RESULT,
                Opcode.IF_EQZ,
                Opcode.INVOKE_INTERFACE,
                Opcode.MOVE_RESULT_OBJECT
            )
        )
    )
) {
    override fun execute(patcherData: PatcherData): PatchResult {
        // Instead of removing all instructions like Vanced,
        // we return the method at the beginning instead
        signatures.first().result!!.method.implementation!!.addInstructions(
            0,
            """
                const/4 v0, 0x1
                return v0
                """.trimIndent().toInstructions()
        )
        return PatchResultSuccess()
    }
}