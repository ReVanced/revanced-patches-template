package app.revanced.patches.reddit.misc.uriparameters.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.reddit.misc.uriparameters.annotations.UriParametersCompatibility
import app.revanced.patches.reddit.misc.uriparameters.fingerprints.ShareLinkFactoryFingerprint
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("uri-parameter-reddit")
@Description("Remove uri parameter when sharing links in reddit.")
@UriParametersCompatibility
@Version("0.0.1")
class UriParametersPatch : BytecodePatch(
    listOf(
        ShareLinkFactoryFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        ShareLinkFactoryFingerprint.result?.let {
            it.mutableMethod.apply {
                val startIndex = it.scanResult.patternScanResult!!.startIndex
                val endIndex = it.scanResult.patternScanResult!!.endIndex

                val parameter = (instruction(endIndex - 1) as BuilderInstruction35c).reference

                if (!parameter.toString().endsWith("Ljava/lang/String;"))
                    return PatchResultError("Method signature parameter did not match: $parameter")

                val matcherRegister = getRegister(startIndex)
                val tempRegister = getRegister(startIndex + 1)
                val uriRegister = getRegister(endIndex)

                addInstructions(
                    endIndex + 1,
                    """
                        const-string v$tempRegister, ".?utm_source=.+"
                        invoke-static {v$tempRegister}, Ljava/util/regex/Pattern;->compile(Ljava/lang/String;)Ljava/util/regex/Pattern;
                        move-result-object v$matcherRegister
                        invoke-virtual {v$matcherRegister, v$uriRegister}, Ljava/util/regex/Pattern;->matcher(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
                        move-result-object v$matcherRegister
                        const-string v$tempRegister, ""
                        invoke-virtual {v$matcherRegister, v$tempRegister}, Ljava/util/regex/Matcher;->replaceAll(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$uriRegister
                        """
                )
            }
        } ?: return ShareLinkFactoryFingerprint.toErrorResult()

        return PatchResultSuccess()
    }

    private fun MutableMethod.getRegister(index: Int):Int {
        return (instruction(index) as OneRegisterInstruction).registerA
    }
}
