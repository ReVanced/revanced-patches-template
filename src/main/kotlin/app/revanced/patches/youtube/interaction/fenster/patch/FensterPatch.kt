package app.revanced.patches.youtube.interaction.fenster.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.interaction.fenster.annotation.FensterCompatibility
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.MethodReference

@Patch
@Name("fenster-swipe-controls")
@Description("volume and brightness swipe controls")
@FensterCompatibility
@Version("0.0.1")
class FensterPatch : BytecodePatch(
    listOf()
) {
    override fun execute(data: BytecodeData): PatchResult {
        // apply the patch to all classes named 'WatchWhileActivity'
        data.classes.filter { it.type.endsWith("WatchWhileActivity;") }.forEach { classDef ->
            data.proxy(classDef).resolve().methods.filter { it.name == "attachBaseContext" }.forEach { methodDef ->
                // in the method, there'll be a super call. we'll have to insert our call after it
                val (superCallIndex, _) = methodDef.implementation!!.instructions.withIndex().first {
                    ((it.value as? ReferenceInstruction)?.reference as? MethodReference)?.name == "attachBaseContext"
                }

                // insert the hook call after the super call
                methodDef.addInstruction(
                    superCallIndex + 1,
                    "invoke-static { p0 }, Lapp/revanced/integrations/fenster/FensterHooksEX;->WatchWhileActivity_onCreateHookEX(Ljava/lang/Object;)V"
                )
            }
        }

        return PatchResultSuccess()
    }
}