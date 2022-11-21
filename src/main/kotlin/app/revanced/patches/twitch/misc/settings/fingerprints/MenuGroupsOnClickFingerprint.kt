package app.revanced.patches.twitch.misc.settings.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.twitch.misc.settings.annotations.SettingsCompatibility
import org.jf.dexlib2.AccessFlags

@Name("settings-menu-groups-onclick-fingerprint")
@SettingsCompatibility
@Version("0.0.1")
object MenuGroupsOnClickFingerprint : MethodFingerprint(
    "V",
    AccessFlags.PRIVATE or AccessFlags.STATIC or AccessFlags.FINAL,
    listOf("L", "L", "L"),
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/SettingsMenuViewDelegate;")
                && methodDef.name.contains("render")
    }
)
