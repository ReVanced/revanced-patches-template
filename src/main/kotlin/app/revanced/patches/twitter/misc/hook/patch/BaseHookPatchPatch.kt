package app.revanced.patches.twitter.misc.hook.patch

import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.twitter.misc.hook.json.patch.JsonHookPatch

@DependsOn([JsonHookPatch::class])
abstract class BaseHookPatchPatch(private val hookClassDescriptor: String) : BytecodePatch() {
    override fun execute(context: BytecodeContext) = try {
        PatchResult.Success.also { JsonHookPatch.Hook(context, hookClassDescriptor).add() }
    } catch (ex: Exception) {
        PatchResult.Error(ex)
    }
}