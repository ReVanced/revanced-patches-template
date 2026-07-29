package app.revanced.patches.dcinside.misc.update

import app.revanced.patcher.*
import app.revanced.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.AccessFlags

internal val BytecodePatchContext.updateCheckMethod by gettingFirstMethodDeclaratively {
    accessFlags(AccessFlags.PRIVATE, AccessFlags.FINAL)
    parameterTypes("Ljava/lang/String;")
    returnType("Z")
    strings("\\.")
    literal(10000)
}