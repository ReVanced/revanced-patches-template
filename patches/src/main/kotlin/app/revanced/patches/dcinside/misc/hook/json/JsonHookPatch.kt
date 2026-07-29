package app.revanced.patches.dcinside.misc.hook.json

import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstructions
import app.revanced.patcher.firstImmutableClassDef
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.dcinside.misc.extension.sharedExtensionPatch
import java.io.InvalidClassException

/**
 * Add a hook to the [jsonHookPatch].
 * Will not add the hook if it's already added.
 *
 * @param jsonHook The [JsonHook] to add.
 */
fun BytecodePatchContext.addJsonHook(
    jsonHook: JsonHook,
) {
    if (jsonHook.added) return

    // Insert hooks right before calling buildList.
    val addIndex = jsonHookPatchMethodMatch[-1]

    jsonHookPatchMethodMatch.method.addInstructions(
        addIndex + 1,
        """
            sget-object v1, ${jsonHook.descriptor}->INSTANCE:${jsonHook.descriptor}
            invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z
        """,
    )

    jsonHook.added = true
}

private const val JSON_HOOK_CLASS_NAMESPACE = "app/revanced/extension/dcinside/patches/hook/json"
internal const val JSON_HOOK_PATCH_CLASS_DESCRIPTOR = "L$JSON_HOOK_CLASS_NAMESPACE/JsonHookPatch;"
private const val BASE_PATCH_CLASS_NAME = "BaseJsonHook"
private const val JSON_HOOK_CLASS_DESCRIPTOR = "L$JSON_HOOK_CLASS_NAMESPACE/$BASE_PATCH_CLASS_NAME;"

val jsonHookPatch = bytecodePatch(
    description = "Hooks the stream which reads JSON responses.",
) {
    dependsOn(sharedExtensionPatch)

    apply {
        jsonHookPatchMethodMatch.methodOrNull
            ?: throw PatchException("Unexpected extension.")

        jsonHookMethod.addInstructions(
            0,
            """
                invoke-static/range { p12 .. p12 }, $JSON_HOOK_PATCH_CLASS_DESCRIPTOR->jsonHook(Ljava/lang/String;)Ljava/lang/String;
                move-result-object p12
            """
        )
    }

    afterDependents {
        val getDummyHookIndex = jsonHookPatchMethodMatch[0]

        // Remove 2 instructions that add DummyHook.
        jsonHookPatchMethodMatch.method.removeInstructions(getDummyHookIndex, 2)
    }
}

class JsonHook internal constructor(
    internal val descriptor: String,
) {
    internal var added = false
}

/**
 * Create a hook class.
 * The class has to extend on **JsonHook**.
 * The class has to be a Kotlin object class, or at least have an INSTANCE field of itself.
 *
 * @param descriptor The class descriptor of the hook.
 * @throws ClassNotFoundException If the class could not be found.
 */
context(context: BytecodePatchContext)
fun jsonHook(descriptor: String): JsonHook {
    context.firstImmutableClassDef(descriptor).let {
        it.also { classDef ->
            if (
                classDef.superclass != JSON_HOOK_CLASS_DESCRIPTOR ||
                !classDef.fields.any { field -> field.name == "INSTANCE" }
            ) {
                throw InvalidClassException(classDef.type, "Not a hook class")
            }
        }
    }

    return JsonHook(descriptor)
}