package app.revanced.patches.youtube.misc.integrations.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.implementation.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.or
import app.revanced.patcher.patch.implementation.BytecodePatch
import app.revanced.patcher.patch.implementation.misc.PatchResult
import app.revanced.patcher.patch.implementation.misc.PatchResultError
import app.revanced.patcher.patch.implementation.misc.PatchResultSuccess
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patcher.util.smali.toInstructions
import app.revanced.patches.youtube.misc.integrations.annotations.IntegrationsCompatibility
import app.revanced.patches.youtube.misc.integrations.signatures.InitSignature
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.immutable.ImmutableMethod
import org.jf.dexlib2.immutable.ImmutableMethodImplementation

@Name("integrations")
@Description("Applies mandatory patches to implement the ReVanced integrations into the application.")
@IntegrationsCompatibility
@Version("0.0.1")
class IntegrationsPatch : BytecodePatch(
    listOf(
        InitSignature
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        if (data.findClass("Lapp/revanced/integrations/utils/ReVancedUtils") == null)
            return PatchResultError("Integrations have not been merged yet. This patch can not succeed without the integrations.")

        val result = InitSignature.result!!

        val implementation = result.method.implementation!!
        val count = implementation.registerCount - 1

        result.method.addInstructions(
            result.scanResult.endIndex + 1, """
                  invoke-static {v$count}, Lapp/revanced/integrations/sponsorblock/StringRef;->setContext(Landroid/content/Context;)V
                  sput-object v$count, Lapp/revanced/integrations/utils/ReVancedUtils;->context:Landroid/content/Context;
            """
        )

        val classDef = result.definingClassProxy.resolve()
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
                    """.trimIndent().toInstructions(), null, null
                )
            ).toMutable()
        )
        return PatchResultSuccess()
    }
}