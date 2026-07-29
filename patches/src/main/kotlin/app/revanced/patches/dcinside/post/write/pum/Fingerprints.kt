package app.revanced.patches.dcinside.post.write.pum

import app.revanced.patcher.*
import app.revanced.patcher.invoke
import app.revanced.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.Opcode

internal val BytecodePatchContext.postWriteActivityHelperMethodMatch by composingFirstMethod {
    instructions(
        Opcode.IGET_BOOLEAN(),
        Opcode.IGET_BOOLEAN(),
        Opcode.IGET_BOOLEAN(),
        "binding"(),
        "postWriteMiniUsed"(),
    )
}

internal fun BytecodePatchContext.postWriteActivityInitMethodMatch(postWriteActivityDefiningClass: String) = firstMethodComposite {
    definingClass(postWriteActivityDefiningClass)
    name("<init>")
}