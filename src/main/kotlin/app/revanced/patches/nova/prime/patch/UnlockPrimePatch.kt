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


//The magic value to insert for unlocking. See comments and "NOTE" below.
const val premiumSharedPreferenceValue: Int = 512

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

    // This patch works by overwriting a SharedPreferences return value in the code to always return us "512",
    // which makes the app to include all its functionalities. The default value here is "0" which restricts
    // the app to the free version.

    // NOTE: It seems any value would work here which is not "0" to patch the app, because the code checks
    // only for the default "0" value during startup. The number "512" is needed for a weird protection mechanism
    // which would reset the preferences if the value on disk had changed after a restart.


    override fun execute(context: BytecodeContext): PatchResult {
        val result = UnlockPrimeFingerprint.result!!
        val methodImplementation = result.mutableMethod.implementation
        val endIndex = result.scanResult.patternScanResult?.endIndex!!

        //Add instruction right after endIndex
        val addIndex = endIndex + 1

        methodImplementation!!.addInstruction(
            addIndex,
            BuilderInstruction21s(
                Opcode.CONST_16, (methodImplementation.instructions[endIndex] as BuilderInstruction11x).registerA, premiumSharedPreferenceValue)
        )

        return PatchResultSuccess()
    }
}
