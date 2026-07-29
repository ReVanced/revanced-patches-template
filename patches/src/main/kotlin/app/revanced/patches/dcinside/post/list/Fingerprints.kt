package app.revanced.patches.dcinside.post.list

import app.revanced.patcher.*
import app.revanced.patcher.invoke
import app.revanced.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val BytecodePatchContext.postItemBindMethodMatch by composingFirstMethod {
    accessFlags(AccessFlags.PRIVATE, AccessFlags.FINAL)
    returnType("V")
    instructions(
        method { returnType == "Lcom/dcinside/app/response/PostItem;" },
        Opcode.MOVE_RESULT_OBJECT(),
        "null cannot be cast to non-null type com.dcinside.app.post.fragments.PostListItemHolder"(),
        method {
            parameterTypes.isEmpty() && returnType == "Ljava/lang/String;" && definingClass == "Lcom/dcinside/app/response/PostItem;"
        },
        method {
            parameterTypes.isEmpty() && returnType == "I" && definingClass == "Lcom/dcinside/app/response/PostItem;"
        },
        Opcode.MOVE_RESULT(),
        "dcbest"(),
        method { name == "getVisibility" },
        method { returnType == "Landroid/text/Spannable;" },
        Opcode.MOVE_RESULT_OBJECT(),
    )
}

internal val BytecodePatchContext.postSearchItemBindMethodMatch by composingFirstMethod {
    instructions(
        method { returnType == "Lcom/dcinside/app/response/PostItem;" },
        Opcode.MOVE_RESULT_OBJECT(),
        ""(),
        "key"(),
        "dcbest"(),
        allOf(
            Opcode.INVOKE_VIRTUAL(),
            method { definingClass == "Lcom/dcinside/app/response/PostItem;" && returnType == "Ljava/lang/String;" }
        ),
        allOf(
            Opcode.INVOKE_VIRTUAL(),
            method { definingClass == "Lcom/dcinside/app/response/PostItem;" && returnType == "Ljava/lang/String;" }
        ),
        method { name == "getVisibility" },
        method { returnType == "Landroid/text/Spannable;" },
        Opcode.MOVE_RESULT_OBJECT(),
    )
}

internal val BytecodePatchContext.addQueryParameterHookMethod by gettingFirstMethodDeclaratively {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    definingClass($$"Lokhttp3/HttpUrl$Builder;")
    name("addQueryParameter")
}

internal val BytecodePatchContext.jsonApiPostListHookMethod by gettingFirstMethodDeclaratively {
    strings("api_postList")
}