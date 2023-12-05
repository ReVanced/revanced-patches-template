package app.revanced.patches.tumblr.annoyances.popups.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

// This method is responsible for loading and displaying the visual Layout of the Gift Message Popup.
internal object ShowGiftMessagePopupFingerprint : MethodFingerprint(
    strings = listOf("activity", "anchorView"),
    customFingerprint = { methodDef, _ -> methodDef.definingClass.endsWith("GiftMessagePopup;") }
)