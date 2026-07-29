package app.revanced.patches.dcinside.misc.signature

import app.revanced.patcher.definingClass
import app.revanced.patcher.gettingFirstMethodDeclaratively
import app.revanced.patcher.patch.BytecodePatchContext

internal val BytecodePatchContext.applicationMethod by gettingFirstMethodDeclaratively {
    definingClass("Lcom/dcinside/app/Application;")
}
