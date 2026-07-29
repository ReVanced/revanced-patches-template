package app.revanced.patches.dcinside.misc.hook

@Suppress("unused")
val hideMustReadNoticePatch = hookPatch(
    name = "Hide must read notice",
    hookClassDescriptor = "Lapp/revanced/extension/dcinside/patches/hook/patch/HideMustReadNoticeHook;"
)