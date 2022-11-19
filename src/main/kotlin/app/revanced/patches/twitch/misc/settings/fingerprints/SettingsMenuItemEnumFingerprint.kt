package app.revanced.patches.twitch.misc.settings.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.twitch.misc.settings.annotations.TwitchSettingsCompatibility

@Name("settings-menu-item-enum-fingerprint")
@TwitchSettingsCompatibility
@Version("0.0.1")
object SettingsMenuItemEnumFingerprint : MethodFingerprint(
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/SettingsMenuItem;") && methodDef.name == "<clinit>"
    }
)
