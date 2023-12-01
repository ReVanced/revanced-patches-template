package app.revanced.patches.reddit.customclients.infinityforreddit.api.fingerprints

internal object LoginActivityOnCreateFingerprint : AbstractClientIdFingerprint(custom@{ methodDef, classDef ->
    methodDef.name == "onCreate" && classDef.type.endsWith("LoginActivity;")
})