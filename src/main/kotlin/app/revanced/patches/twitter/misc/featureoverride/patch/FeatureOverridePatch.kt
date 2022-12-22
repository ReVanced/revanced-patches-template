package app.revanced.patches.twitter.misc.featureoverride.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.twitter.misc.featureoverride.fingerprints.FeatureOverrideFingerprint
import app.revanced.patches.twitter.misc.featureoverride.annotations.FeatureOverrideCompatibility
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.MethodReference


@Patch
@Name("feature-override")
@Description("Overrides Feature Flags for Twitter")
@FeatureOverrideCompatibility
@Version("0.0.1")
class FeatureOverridePatch : BytecodePatch(
    listOf(
        FeatureOverrideFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        FeatureOverrideFingerprint.result!!.mutableMethod.addInstructions(
            0, """
                invoke-static { p1 }, Lapp/revanced/integrations/patches/FeatureOverridePatch;->overrideFeature(Ljava/lang/String;)Z
                move-result v0
                if-eqz v0, :dontoverride
                const/4 v0, 0
                return v0
                :dontoverride
                nop
                """
        )

        return PatchResultSuccess()
    }
}
