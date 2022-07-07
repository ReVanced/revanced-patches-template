package app.revanced.extensions

import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.smali.toInstruction
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.MutableMethodImplementation
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n
import org.jf.dexlib2.builder.instruction.BuilderInstruction11x
import org.jf.dexlib2.builder.instruction.BuilderInstruction21t
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c
import org.jf.dexlib2.immutable.reference.ImmutableMethodReference
import org.w3c.dom.Node

internal fun MutableMethodImplementation.injectHideCall(
    index: Int,
    register: Int
) {
    this.addInstruction(
        index,
        "invoke-static { v$register }, Lapp/revanced/integrations/patches/HideHomeAdsPatch;->HideHomeAds(Landroid/view/View;)V".toInstruction()
    )
}

/**
 * Insert an event hook at the top of the method. If the hook returns true, the event is consumed and the method will return with true
 *
 * the hook method MUST return a boolean and MUST take two parameters, like so:
 *  fun hook(thisRef: Object, eventData: Object): Boolean {}
 *
 * The final injected code will resemble the following logic:
 *  if( YouHook(this, event) ) { return true; }
 *  ...
 *
 * @param hookRef reference to the hook method
 */
internal fun MutableMethod.injectConsumableEventHook(hookRef: ImmutableMethodReference) {
    val isStaticMethod = AccessFlags.STATIC.isSet(this.accessFlags)
    this.implementation?.let { impl ->
        // create label to index 0 to continue to the normal program flow
        val lblContinueNormalFlow = impl.newLabelForIndex(0)

        // define registers
        /** V0 */
        val regV0 = 0

        /** this  */
        val regP0 = impl.registerCount - this.parameters.size - (if (isStaticMethod) 0 else 1)

        /** motionEvent */
        val regP1 = regP0 + 1

        // insert instructions at the start of the method:
        // if( Hook(this, event) ) { return true; }
        impl.addInstructions(
            0, listOf(
                // invoke-static { p0, p1 } <hook>
                BuilderInstruction35c(
                    Opcode.INVOKE_STATIC,
                    2,
                    regP0,
                    regP1,
                    0, 0, 0,
                    hookRef
                ),

                // move-result v0
                BuilderInstruction11x(
                    Opcode.MOVE_RESULT,
                    regV0
                ),

                // if-eqz v0, :continue_normal_flow
                BuilderInstruction21t(
                    Opcode.IF_EQZ,
                    regV0,
                    lblContinueNormalFlow
                ),

                // const/4 v0, 0x1
                BuilderInstruction11n(
                    Opcode.CONST_4,
                    regV0,
                    0x1
                ),

                // return v0
                BuilderInstruction11x(
                    Opcode.RETURN,
                    regV0
                )

                // :continue_normal_flow
            )
        )
    }
}

internal fun Node.doRecursively(action: (Node) -> Unit) {
    action(this)
    for (i in 0 until this.childNodes.length) this.childNodes.item(i).doRecursively(action)
}

internal fun String.startsWithAny(vararg prefixes: String): Boolean {
    for (prefix in prefixes)
        if (this.startsWith(prefix))
            return true

    return false
}

internal fun String.equalsAny(vararg other: String): Boolean {
    for (_other in other)
        if (this == _other)
            return true

    return false
}
