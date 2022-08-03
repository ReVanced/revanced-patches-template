package app.revanced.patches.youtube.interaction.swipecontrols.patch.bytecode

import app.revanced.extensions.transformMethods
import app.revanced.extensions.traverseClassHierarchy
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.interaction.swipecontrols.annotation.SwipeControlsCompatibility
import app.revanced.patches.youtube.interaction.swipecontrols.fingerprints.WatchWhileActivityFingerprint
import app.revanced.patches.youtube.interaction.swipecontrols.patch.resource.SwipeControlsResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.playertype.patch.PlayerTypeHookPatch
import org.jf.dexlib2.AccessFlags

@Patch
@Name("swipe-controls")
@Description("Adds volume and brightness swipe controls.")
@SwipeControlsCompatibility
@Version("0.0.3")
@DependsOn(
    [
        IntegrationsPatch::class,
        PlayerTypeHookPatch::class,
        SwipeControlsResourcePatch::class
    ]
)
class SwipeControlsBytecodePatch : BytecodePatch(
    listOf(
        WatchWhileActivityFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val wrapperClass = data.findClass(
            "Lapp/revanced/integrations/swipecontrols/SwipeControlsHostActivity;"
        )!!.resolve()
        val targetClass = WatchWhileActivityFingerprint.result!!.mutableClass

        // inject the wrapper class from integrations into the class hierarchy of WatchWhileActivity
        wrapperClass.setSuperClass(targetClass.superclass)
        targetClass.setSuperClass(wrapperClass.type)

        // ensure all classes and methods in the hierarchy are non-final, so we can override them in integrations
        data.traverseClassHierarchy(targetClass) {
            accessFlags = accessFlags and AccessFlags.FINAL.value.inv()
            transformMethods {
                setAccessFlags(accessFlags and AccessFlags.FINAL.value.inv())
                this
            }
        }
        return PatchResultSuccess()
    }
}

