package app.revanced.patches.instagram.patches.integrations.patch
import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.instagram.patches.integrations.fingerprints.ReflectionFingerprint
import org.jf.dexlib2.dexbacked.reference.DexBackedFieldReference
import org.jf.dexlib2.dexbacked.reference.DexBackedTypeReference
import org.jf.dexlib2.iface.instruction.formats.Instruction21c

@Patch
@Name("fix-integrations")
@Description("Stops app from crashing when integrations are merged.")
@Compatibility([Package("com.instagram.android")])
@Version("0.0.1")
class FixIntegrationsPatch : BytecodePatch(
    listOf(
        ReflectionFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        ReflectionFingerprint.apply {  ->
            val startIndex = result?.scanResult?.patternScanResult?.startIndex
                ?: return toErrorResult()

            val reference = ((result!!.method.implementation!!.instructions.elementAt(startIndex) as Instruction21c).reference as DexBackedTypeReference).type
            val field = ((result!!.method.implementation!!.instructions.elementAt(startIndex+2) as Instruction21c).reference as DexBackedFieldReference)
            val fieldname = field.name
            val classname = field.definingClass


            result!!.mutableMethod.addInstructions(0, """
                new-instance        v1, $reference
                invoke-direct       {v1}, $reference-><init>()V
                sput-object         v1, $classname->$fieldname:$reference
                return-void 
            """.trimIndent())
        }

        return PatchResultSuccess()
    }
}