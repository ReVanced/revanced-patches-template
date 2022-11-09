# 🧱 Skeleton of a [ReVanced patch](https://github.com/revanced/revanced-patches)

Get a basic understanding about the structure of a patch.

## ⛳️ Example patch

This page works with the following patch as an example:

```kt
package app.revanced.patches.ads.patch

// Imports

@Patch
@Name("disable-ads")
@Description("Disables ads.")
@DependsOn([HideAdResourcePatch:class])
@Compatibility([Package("com.some.app", arrayOf("0.1.0"))])
@Version("0.0.1")
class DisableAdsPatch : BytecodePatch(
    listOf(LoadAdsFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = LoadAdsFingerprint.result
            ?: return PatchResultError("LoadAdsFingerprint not found")

        result.mutableMethod.replaceInstructions(
                0,
                """
                    const/4 v0, 0x1
                    return v0
                """
        )

        return PatchResultSuccess()
    }
}
```

## 🔎 Dissecting the example patch

Lets start with understanding, how a patch is structured. A patch is mainly built out of three components:

### 1. 📝 Patch annotations

```kt
@Patch
@Name("disable-ads")
@Description("Disables ads.")
@DependsOn([HideAdResourcePatch:class])
@Compatibility([Package("com.some.app", arrayOf("0.1.0"))])
@Version("0.0.1")
```

To give context about the patch, annotations are used. They serve different but important purposes:

- Every visible patch **should** be annotated with `@Patch` to be picked up by `PatchBundle` from the [introduction](1_introduction.md). Patches which are not annotated with `@Patch` can be referenced by other patches. We refer to those as _patch dependencies_. Patch dependencies are useful to structure multiple patches.

  Example: _To add settings switches to an app, first, a patch is required which can provide a basic framework for other patches to add their toggles to that app. For that, those patches refer to the dependency patch and use its framework to add their toggles to an app. The [ReVanced patcher](https://github.com/revanced/revanced-patcher) will then first execute the dependency and then the patch itself. The dependency can prepare a preference screen when executed and then initialize the its framework for further use by other patches._

- Visible patches **should** be annotated with `@Name`. This annotation does not serve any functional purpose. Instead, it allows to refer to the patch with a name. The [ReVanced patches](https://github.com/revanced/revanced-patches) use _Kebab casing_ by convention, but any name can be used for patches. Patches with no `@Patch` annotation do not require the `@Name` annotation, because they are only useable as dependencies for other patches, and therefore are not visible through `PatchBundle`.

- Visible patches should be annotated with `@Description`. This annotation serves the same purpose as the annotation `@Name`. It is used to give the patch a short description.

- Patches can be annotated with `@DependsOn`. If the current patch depends on other patches, it can declare them as dependencies.

  Example: _The patch to remove ads needs to patch the bytecode. Additionally it makes use of a second patch, to get rid of resource files in the app which show ads in the app._

- **All patches** should be annotated with `@Compatibility`. This annotation is the most complex, but **most important** one and serves the purpose of constraining a patch to a package. Every patch is compatible with usually one or more packages. Additionally, the constraint can optionally be extended to versions of the package to discourage the use of the patch with versions outside of the constraint.

  Example: _The patch disables ads for an app. The app regularly updates and the code of the app mutates heavily. In that case the patch might not be compatible for future, untested versions of the app. To discourage the use of the app with other versions than the versions, this patch was confirmed to work on, it is constrained to those versions only._

- Patches can be annotated with `@Version`.

  > Currently, this annotation does not serve any purpose, but is added to patches by convention, in case a use case has been found.

### 2. 🏗️ Patch classes

```kt
class DisableAdsPatch : BytecodePatch( /* Parameters */ ) {
   // Code
}
```

Usually, patches consist out of a single class. The class can be used to create methods and fields for the patch, or provide a framework for other patches, in case it is meant to be used as a dependency patch.

The [ReVanced patches](https://github.com/revanced/revanced-patches) follow a convention to name the class of patches:

Example: _The class for a patch which disables ads should be called `DisableAdsPatch`, for a patch which adds a new download feature it should be called `DownloadsPatch`._

Each patch implicitly implements the [Patch](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/patch/Patch.kt#L15) interface when extending off [ResourcePatch](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/patch/Patch.kt#L35) or [BytecodePatch](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/patch/Patch.kt#L42). The current example extends off `BytecodePatch`:

```kt
class DisableAdsPatch : BytecodePatch( /* Parameters */ ) {
   // Code
}
```

If the patch extends off `ResourcePatch`, it is able to **patch resources** such as `XML`, `PNG` or similar files. On the other hand, if the patche extends off `BytecodePatch`, it is able to **patch the bytecode** of an app. If a patch needs access to the resources and the bytecode at the same time. Either can use the other as a dependency. **Circular dependencies are unhandled.**

### 3. 🏁 The `execute` method

```kt
override fun execute(context: BytecodeContext): PatchResult {
   // Code
}
```

The `execute` method is declared in the `Patch` interface and therefore part of any patch:

```kt
fun execute(context: /* Omitted */ T): PatchResult
```

It is the **first** method executed when running the patch. The current example extends off `BytecodePatch`. Since patches which extend on it can interact with the bytecode, the signature for the execute method when implemented requires a [BytecodeContext](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/data/Context.kt#L23) as a parameter:

```kt
override fun execute(context: BytecodeContext): PatchResult {
   // Code
}
```

The `BytecodeContext` contains everything necessary related to bytecode for patches, including every class of the app which the patch will be applied on. Likewise, a `ResourcePatch` will require a [ResourceContext](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/data/Context.kt#L89) parameter and provide the patch with everything necessary to patch resources.

The `execute` method has to be returned with [PatchResult](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/patch/PatchResult.kt#L3). Patches can return early with `PatchResultError` if something went wrong. If this patch is used as a dependency for other patches, those patches will not execute subsequently. If a patch succeeds `PatchResultSuccess` is expected to be returned.

In the current example the `execute` method runs the following code to replace instructions at the index `0` of the methods instruction list:

```kt
val result = LoadAdsFingerprint.result
   ?: return PatchResultError("LoadAdsFingerprint not found")

result.mutableMethod.replaceInstructions(
        0,
        """
            const/4 v0, 0x1
            return v0
        """
)
return PatchResultSuccess()
```

> Details of this implementation and what exactly `Fingerprints` are will be introduced properly in another page.

## 🤏 Minimal template for a bytecode patch

```kt
package app.revanced.patches.examples.minimal.patch

// Imports

@Patch
@Name("minimal-demonstration")
@Description("Demonstrates a minimal implementation of a patch.")
@Compatibility([Package("com.some.app")])
class MinimalExamplePatch : BytecodePatch() {
    override fun execute(context: BytecodeContext) {
      println("${MinimalExamplePatch::class.patchName} is being executed." )

      return PatchResultSuccess()
    }
}
```

## ⏭️ Whats next

The next section will introduce the concept of fingerprinting methods.

Continue: [Introduction to the ReVanced patcher](1_introduction.md)
