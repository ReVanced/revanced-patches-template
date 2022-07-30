package app.revanced.patches.youtube.misc.integrations.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.data.impl.toMethodWalker
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patcher.util.smali.toInstructions
import app.revanced.patches.youtube.misc.integrations.annotations.IntegrationsCompatibility
import app.revanced.patches.youtube.misc.integrations.fingerprints.InitFingerprint
import app.revanced.patches.youtube.misc.integrations.fingerprints.ServiceFingerprint
import app.revanced.patches.youtube.misc.integrations.fingerprints.ServiceParentFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.immutable.ImmutableMethod
import org.jf.dexlib2.immutable.ImmutableMethodImplementation

@Name("integrations")
@Description("Applies mandatory patches to implement the ReVanced integrations into the application.")
@IntegrationsCompatibility
@Version("0.0.1")
class IntegrationsPatch : BytecodePatch(
    listOf(
        InitFingerprint,
        ServiceParentFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val initMethod = InitFingerprint.result!!

        val serviceParentMethod = ServiceParentFingerprint.result!!.classDef
        ServiceFingerprint.resolve(data, serviceParentMethod)
        val serviceMethod =
            data.toMethodWalker(ServiceFingerprint.result!!.method)
                .nextMethod(ServiceFingerprint.result!!.patternScanResult!!.startIndex, true)
                .getMethod() as MutableMethod

        if (data.findClass("Lapp/revanced/integrations/utils/ReVancedUtils") == null)
            return PatchResultError("Integrations have not been merged yet. This patch can not succeed without the integrations.")

        val implementation = initMethod.mutableMethod.implementation!!
        val count = implementation.registerCount - 1
        initMethod.mutableMethod.addInstruction(
            0,
            "sput-object v$count, Lapp/revanced/integrations/utils/ReVancedUtils;->context:Landroid/content/Context;"
        )
        
        val classDef = initMethod.mutableClass
        classDef.methods.add(
            ImmutableMethod(
                classDef.type,
                "getAppContext",
                null,
                "Landroid/content/Context;",
                AccessFlags.PUBLIC or AccessFlags.STATIC,
                null,
                null,
                ImmutableMethodImplementation(
                    1, """
                        invoke-static { }, Lapp/revanced/integrations/utils/ReVancedUtils;->getAppContext()Landroid/content/Context;
                        move-result-object v0
                        return-object v0
                    """.toInstructions(), null, null
                )
            ).toMutable()
        )

        val serviceIndex = serviceMethod.implementation!!.instructions.size
        serviceMethod.addInstructions(
            serviceIndex, """
               invoke-static {}, Lcom/google/android/apps/youtube/app/YouTubeTikTokRoot_Application;->getAppContext()Landroid/content/Context;
               move-result-object p0
               sput-object p0, Lapp/revanced/integrations/utils/ReVancedUtils;->context:Landroid/content/Context;
            """
        )
        return PatchResultSuccess()
    }
}
