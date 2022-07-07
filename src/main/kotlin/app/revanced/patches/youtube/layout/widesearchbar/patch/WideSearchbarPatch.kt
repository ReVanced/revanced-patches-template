package app.revanced.patches.youtube.layout.widesearchbar.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.layout.widesearchbar.annotations.WideSearchbarCompatibility
import app.revanced.patches.youtube.layout.widesearchbar.fingerprints.methodone.WideSearchbarOneFingerprint
import app.revanced.patches.youtube.layout.widesearchbar.fingerprints.methodone.WideSearchbarOneParentFingerprint
import app.revanced.patches.youtube.layout.widesearchbar.fingerprints.methodtwo.WideSearchbarTwoFingerprint
import app.revanced.patches.youtube.layout.widesearchbar.fingerprints.methodtwo.WideSearchbarTwoParentFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch

@Patch
@Dependencies(dependencies = [IntegrationsPatch::class])
@Name("enable-wide-searchbar")
@Description("Replaces the search-icon with a wide searchbar.")
@WideSearchbarCompatibility
@Version("0.0.1")
class WideSearchbarPatch : BytecodePatch(
    listOf(
        WideSearchbarOneParentFingerprint, WideSearchbarTwoParentFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        WideSearchbarOneFingerprint.resolve(data, WideSearchbarOneParentFingerprint.result!!.classDef)
        WideSearchbarTwoFingerprint.resolve(data, WideSearchbarTwoParentFingerprint.result!!.classDef)

        val methodOne = WideSearchbarOneFingerprint.result
            ?: return PatchResultError("Required Parent method for methodOne could not be found.")

        val methodTwo = WideSearchbarTwoFingerprint.result
            ?: return PatchResultError("Required Parent method for methodTwo could not be found.")

        //ToDo: stuff

        return PatchResultError("Patch not finished yet!")
    }

    //MethodOne: in class fbn
    /*
     public static boolean aF(txm txmVar) {
        akfd akfdVar = txmVar.b().e;
        if (akfdVar == null) {
        akfdVar = akfd.a;
     }
     return app.revanced.integrations.patches.NewActionbarPatch.getNewActionBar();
   }
   */

    //MethodTwo: in class fbn
    /*
    public static boolean aB(txm txmVar) {
        akfd akfdVar = txmVar.b().e;
        if (akfdVar == null) {
            akfdVar = akfd.a;
        }
        return app.revanced.integrations.patches.NewActionbarPatch.getNewActionBar();
    }
    */
}
