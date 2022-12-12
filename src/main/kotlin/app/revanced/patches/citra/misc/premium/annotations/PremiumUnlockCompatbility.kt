package app.revanced.patches.citra.misc.premium.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("org.citra.citra_emu"), Package("org.citra.citra_emu.canary")])
internal annotation class PremiumUnlockCompatbility
