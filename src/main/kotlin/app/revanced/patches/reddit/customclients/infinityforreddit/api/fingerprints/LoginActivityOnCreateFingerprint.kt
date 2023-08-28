package app.revanced.patches.reddit.customclients.infinityforreddit.api.fingerprints

object LoginActivityOnCreateFingerprint : AbstractClientIdFingerprint(
    returnType = "V",
    customFingerprint = custom@{ methodDef, classDef ->
        if (!classDef.type.endsWith("LoginActivity;")) return@custom false

        methodDef.name == "onCreate"
    }
)