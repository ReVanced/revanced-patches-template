package app.revanced.patches.all.ads.bytecode

import org.jf.dexlib2.Opcode

val blockUrls: List<String> = listOf(
    "adservice.google.com",
    "g.doubleclick.net",
    "googleadservices.com",
    "amazon-adsystem.com",
)

val replaceUrlsWith: String = "https://example.com"

val stringOpcodes: List<Opcode> = listOf(
    Opcode.CONST_STRING
)

// https://developers.google.com/admob/android/banner
val blockInvokes: List<String> = listOf(
    "loadAd",
    "AdListener",
    "AdLoad",
    "AdView",
)

val invokeOpcodes: List<Opcode> = listOf(
    Opcode.INVOKE_VIRTUAL,
    Opcode.INVOKE_SUPER,
    Opcode.INVOKE_DIRECT,
    Opcode.INVOKE_STATIC,
    Opcode.INVOKE_INTERFACE
)