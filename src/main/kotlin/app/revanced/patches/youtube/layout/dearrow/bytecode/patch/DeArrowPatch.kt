package app.revanced.patches.youtube.layout.dearrow.bytecode.patch

import app.revanced.extensions.findMutableMethodOf
import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.dearrow.annotations.DeArrowCompatibility
import app.revanced.patches.youtube.layout.dearrow.bytecode.fingerprints.ImageUrlStringToURIFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import org.jf.dexlib2.AccessFlags

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("DeArrow")
@DeArrowCompatibility
@Description("Integrates DeArrow to show video thumbnails that better represent the video content")
class DeArrowPatch : BytecodePatch(
    listOf(
        ImageUrlStringToURIFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        ImageUrlStringToURIFingerprint.result?.apply {
            mutableMethod.addInstructions(
                0,
                """
                    invoke-static {p0}, $INTEGRATIONS_CLASS_DESCRIPTOR;->overrideImageURL(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object p0
                """
            )
        } ?: return ImageUrlStringToURIFingerprint.toErrorResult()


        if (true) return PatchResultSuccess()

        // Hook every String parameter and log it
        context.classes.forEach { classDef ->
            if (classDef.type.contains("/") && !classDef.type.contains("youtube")) return@forEach

            classDef.methods.forEach methodEach@{ method ->
                if (AccessFlags.CONSTRUCTOR.isSet(method.accessFlags)) {
                    return@methodEach
                }

                var parameterIndex: Int
                if (!AccessFlags.STATIC.isSet(method.accessFlags)) {
                    parameterIndex = 1
                } else {
                    parameterIndex = 0
                }

                method.parameters.forEach { methodParameter ->
                    if (methodParameter.type == "Ljava/lang/String;") {
                        println("Class: $classDef method: $method")
                        if (method.implementation != null) {
                            with(context.proxy(classDef).mutableClass) {
                                with(findMutableMethodOf(method)) {
                                    this.addInstruction(
                                        0,
                                        "invoke-static/range {p$parameterIndex .. p$parameterIndex}, $INTEGRATIONS_CLASS_DESCRIPTOR;->overrideImageURL(Ljava/lang/String;)Ljava/lang/String;"
                                    )
                                }
                            }
                        }
                    }
                    parameterIndex++
                    if (methodParameter.type == "J") parameterIndex++
                    if (methodParameter.type == "D") parameterIndex++
                }
            }
        }

        return PatchResultSuccess()
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/DeArrowPatch"
    }
}
