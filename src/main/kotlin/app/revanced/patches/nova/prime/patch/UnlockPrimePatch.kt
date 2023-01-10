package app.revanced.patches.nova.prime.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.nova.prime.annotations.UnlockPrimeCompatibility
import app.revanced.patches.nova.prime.fingerprints.UnlockPrimeFingerprint
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction11x
import org.jf.dexlib2.builder.instruction.BuilderInstruction21s

@Patch
@Name("unlock-prime")
@Description("Unlocks Nova Prime and all functions of the app.")
@UnlockPrimeCompatibility
@Version("0.0.1")
class UnlockPrimePatch : BytecodePatch(
    listOf(
        UnlockPrimeFingerprint
    )
) {

    //Insert the hex value 0x512 right after the pattern which checks the version of the app (paid or free).
    //Free version is '0', paid version is '0x512'.

    override fun execute(context: BytecodeContext): PatchResult {
        val result = UnlockPrimeFingerprint.result!!
        val methodImplementation = result.mutableMethod.implementation
        val startIndex = result.scanResult.patternScanResult?.startIndex!!

        val replaceIndex = startIndex + 5

        methodImplementation!!.addInstruction(
            replaceIndex,
            BuilderInstruction21s(
                Opcode.CONST_16, (methodImplementation.instructions[replaceIndex - 1] as BuilderInstruction11x).registerA, 1298)    // 0x512
        )

        return PatchResultSuccess()
    }
}
