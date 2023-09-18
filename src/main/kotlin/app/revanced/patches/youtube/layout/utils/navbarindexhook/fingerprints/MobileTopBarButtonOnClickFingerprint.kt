package app.revanced.patches.youtube.layout.utils.navbarindexhook.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object MobileTopBarButtonOnClickFingerprint : MethodFingerprint(
    strings = listOf("MenuButtonRendererKey"),
    customFingerprint = { methodDef, _ -> methodDef.name == "onClick" }
)