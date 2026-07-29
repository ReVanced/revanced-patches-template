package app.revanced.patches.dcinside.post.write.pum

import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.fieldReference
import app.revanced.patcher.extensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

@Suppress("unused")
val disablePostPumOptionPatch = bytecodePatch(
    name = "Disable post Pum option",
    description = "Disables the Pum option by default when opening the post write screen.",
) {
    compatibleWith(
        "com.dcinside.app.android"(
            "5.3.2"
        )
    )

    apply {
        var postWriteActivityDefiningClass = ""
        var notAllowedPumEnableFieldReference = ""
        postWriteActivityHelperMethodMatch.let {
            it.method.apply {
                postWriteActivityDefiningClass = definingClass

                val notAllowedPumEnableFieldIndex = it[2]
                notAllowedPumEnableFieldReference = getInstruction<TwoRegisterInstruction>(notAllowedPumEnableFieldIndex).fieldReference!!.toString()
            }
        }

        postWriteActivityInitMethodMatch(postWriteActivityDefiningClass).method.addInstructions(
            0,
            """
                const/4 v0, 0x1
                iput-boolean v0, p0, $notAllowedPumEnableFieldReference
            """
        )
    }
}