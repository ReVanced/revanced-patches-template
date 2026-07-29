package app.revanced.patches.dcinside.ads

import app.revanced.patcher.*
import app.revanced.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.AccessFlags

internal val BytecodePatchContext.shouldLoadAdMethod by gettingFirstMethodDeclaratively {
    parameterTypes("Ljava/lang/String;")
    returnType("Ljava/util/List;")
    strings(
        "getString(...)",
        "GSON",
        "getOrPut(...)",
    )
}

internal val BytecodePatchContext.shouldShowFooterAdMethod by gettingFirstMethodDeclaratively {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returnType("V")
    strings("readFooterAdContainer")
}

internal val BytecodePatchContext.setMinimumHeightMethod by gettingFirstMethodDeclaratively {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    parameterTypes("Ljava/util/List;")
    returnType("I")
    strings("list")
}