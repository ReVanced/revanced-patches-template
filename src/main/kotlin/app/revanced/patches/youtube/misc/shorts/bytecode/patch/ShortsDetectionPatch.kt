package app.revanced.patches.youtube.misc.shorts.bytecode.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.youtube.misc.shorts.bytecode.fingerprints.ShortsPlayerConstructorFingerprint
import app.revanced.patches.youtube.misc.shorts.resource.ShortsDetectionResourcePatch

@Name("shorts-detection")
@DependsOn([ShortsDetectionResourcePatch::class])
@Version("0.0.1")
class ShortsDetectionPatch : BytecodePatch(
    listOf(ShortsPlayerConstructorFingerprint)
) {
    internal companion object {
        fun hookShortsOpened(methodDescriptor: String) {
            val shortsPlayerConstructorMethod = ShortsPlayerConstructorFingerprint.result!!.mutableMethod
            shortsPlayerConstructorMethod.addInstruction(
                0,
                "invoke-static {}, $methodDescriptor"
            )
        }
    }

    override fun execute(context: BytecodeContext): PatchResult {
        ShortsPlayerConstructorFingerprint.result ?: return ShortsPlayerConstructorFingerprint.toErrorResult()
        return PatchResultSuccess()
    }
}