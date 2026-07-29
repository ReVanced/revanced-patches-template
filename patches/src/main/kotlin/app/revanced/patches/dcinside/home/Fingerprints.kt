package app.revanced.patches.dcinside.home

import app.revanced.patcher.*
import app.revanced.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.AccessFlags

internal val BytecodePatchContext.mainSetNewGalleriesMethod by gettingFirstMethodDeclaratively {
    accessFlags(AccessFlags.PUBLIC)
    name("setNewGalleries")
    parameterTypes("Ljava/util/List;")
}