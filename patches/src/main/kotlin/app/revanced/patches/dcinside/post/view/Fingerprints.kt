package app.revanced.patches.dcinside.post.view

import app.revanced.com.android.tools.smali.dexlib2.mutable.MutableMethod
import app.revanced.patcher.*
import app.revanced.patcher.extensions.methodReference
import app.revanced.patcher.invoke
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.util.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.ClassDef

internal val BytecodePatchContext.bottomLikePostsMethod by gettingFirstMethodDeclaratively {
    accessFlags(AccessFlags.PRIVATE, AccessFlags.FINAL)
    returnType("V")
    instructions(
        "vwTitle"(),
        field { type == "Lcom/lsjwzh/widget/recyclerviewpager/LoopRecyclerViewPager;" },
        "vwRecycler"(),
    )
}

internal val BytecodePatchContext.postHeaderSetupMethodMatch by composingFirstMethod {
    parameterTypes("Lcom/dcinside/app/model/PostInfo;", "Z", "Ljava/lang/String;")
    returnType("V")
    instructions(
        "info"(),
        allOf(
            Opcode.INVOKE_VIRTUAL(),
            method { definingClass == "Lcom/dcinside/app/model/PostInfo;" && returnType == "Ljava/lang/String;" }
        ),
        allOf(
            Opcode.INVOKE_VIRTUAL(),
            method { definingClass == "Lcom/dcinside/app/model/PostInfo;" && returnType == "Ljava/lang/String;" }
        ),
        "readHeaderSubject"(),
        "readHeaderMemberIc"(),
        method { returnType == "Ljava/lang/CharSequence;" },
        Opcode.MOVE_RESULT_OBJECT(),
        "readHeaderUserMemo"(),
    )
}

internal val BytecodePatchContext.postReplySetupMethodMatch by composingFirstMethod {
    accessFlags(AccessFlags.PRIVATE)
    returnType("V")
    instructions(
        Opcode.MOVE_OBJECT_FROM16(),
        Opcode.MOVE_OBJECT_FROM16(),
        Opcode.MOVE_OBJECT_FROM16(), // PostReplyItem
        Opcode.INVOKE_VIRTUAL(),
        Opcode.MOVE_RESULT_OBJECT(), // DividerConstraintLayout
        Opcode.MOVE_RESULT_OBJECT(),
        0x3e4ccccdL(),
        0x3f800000L(),
        method { name == "setVisibility" },
        method { name == "setVisibility" },
        method { returnType == "Ljava/lang/String;" }, // user id
        ".*"(),
        "owner"(),
        method { returnType == "Ljava/lang/CharSequence;" },
        Opcode.MOVE_RESULT_OBJECT(),
    )
}

internal val BytecodePatchContext.postHistoryRealmSetupMethodMatch by composingFirstMethod {
    parameterTypes("L", "Lcom/dcinside/app/model/PostInfo;")
    returnType("V")
    instructions(
        "this.where(T::class.java)"(),
        "key"(),
        "this.createObject(T::class.java, primaryKeyValue)"(),
    )
}

context(_: BytecodePatchContext)
internal fun ClassDef.getStringGetterMethod(userIdFieldName: String, compositeMatch: CompositeMatch? = null) = firstMethodDeclaratively {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returnType("Ljava/lang/String;")
    parameterTypes()
    instructions(
        allOf(
            Opcode.IGET_OBJECT(),
            field { name == userIdFieldName }
        ),
        after(
            Opcode.RETURN_OBJECT()
        )
    )
    if (compositeMatch != null) {
        custom {
            compositeMatch.method.indexOfFirstInstruction {
                methodReference?.let {
                    it.definingClass == definingClass && it.name == name
                } == true
            } != -1
        }
    }
}
