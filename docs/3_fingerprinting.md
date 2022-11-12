# 🔎 Fingerprinting

Fingerprinting is the process of creating uniquely identifyable data about something arbitrarily large. In the context of ReVanced, fingerprinting is essential to be able to find classes, methods and fields without knowing their original names or certain other attributes, which would be used to identify them under normal circumstances.

## ⛳️ Example fingerprint

This page works with the following fingerprint as an example:

```kt

package app.revanced.patches.ads.fingerprints

// Imports

object LoadAdsFingerprint : MethodFingerprint(
    returnType = "Z",
    access = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("Z"),
    opcodes = listOf(Opcode.RETURN),
    strings = listOf("pro"),
    customFingerprint = { it.definingClass == "Lcom/some/app/ads/Loader;"}
)
```

## 🆗 Understanding the example fingerprint

The example fingerprint called `LoadAdsFingerprint` which extends on [`MethodFingerprint`](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/fingerprint/method/impl/MethodFingerprint.kt#L28) is made to uniquely identify a certain method by capturing various attributes of the method such as the return type, access flags an opcode pattern and more. The following code can be inferred just from the fingerprint:

```kt
    package com.some.app.ads

    // Imports

 4 <attributes> class Loader {
 5     public final Boolean <methodName>(<field>: Boolean) {
           // ...

 8         val userStatus = "pro";

           // ...

12         return <returnValue>
       }
    }
```

## 🚀 How it works

Each attribute of the fingerprint is responsible to describe a specific but distinct part of the method. The combination out of those should be and ideally remain unique to all methods in all classes. In the case of the example fingerprint, the `customFingerprint` attribute is responsible to find the class the method is defined in. This greatly increases the uniqueness of the fingerprint, because now the possible methods reduce down to that class. Adding the signature of the method and a string the method implementation refers to in combination now creates a unique fingerprint in the current example:

- Package & class (Line 4)

  ```kt
  customFingerprint = { it.definingClass == "Lcom/some/app/ads/Loader;"}
  ```

- Method signature (Line 5)

  ```kt
  returnType = "Z",
  access = AccessFlags.PUBLIC or AccessFlags.FINAL,
  parameters = listOf("Z"),
  ```

- Method implementation (Line 8 & 12)

  ```kt
    strings = listOf("pro"),
    opcodes = listOf(Opcode.RETURN)
  ```

## 🔨 How to use fingerprints

After creating a fingerprint, add it to the constructor of the `BytecodePatch`:

```kt
class DisableAdsPatch : BytecodePatch(
    listOf(LoadAdsFingerprint)
) { /* .. */ }
```

The ReVanced patcher will try to [resolve the fingerprint](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/fingerprint/method/impl/MethodFingerprint.kt#L63) **before** it calls the `execute` method of the patch.

The fingerprint can now be used in the patch by accessing [`MethodFingerprint.result`](https://github.com/revanced/revanced-patcher/blob/d2f91a8545567429d64a1bcad6ca1dab62ec95bf/src/main/kotlin/app/revanced/patcher/fingerprint/method/impl/MethodFingerprint.kt#L227):

```kt
class DisableAdsPatch : BytecodePatch(
    listOf(LoadAdsFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = LoadAdsFingerprint.result
            ?: return PatchResultError("LoadAdsFingerprint not found")

        // ...
    }
}
```

> **Note**: `MethodFingerprint.result` **can be null** if the fingerprint does not match any method. In such case, the fingerprint needs to be fixed and made more resilient if the error is caused by a later version of an app which the fingerprint was not tested on. A fingerprint is good, if it is _light_, but still resilient - like Carbon fiber-reinforced polymers.

If the fingerprint resolved to a method, the following properties are now available:

```kt
data class MethodFingerprintResult(
    val method: Method,
    val classDef: ClassDef,
    val scanResult: MethodFingerprintScanResult,
    // ...
) {
    val mutableClass
    val mutableMethod

    // ...
}
```

> Details on how to use them in a patch and what exactly these are will be introduced properly later on this page.

## 🏹 Different ways to resolve a fingerprint

Usually, fingerprints are mostly resolved by the patcher, but it is also possible to manually resolve a fingerprint in a patch. This can be quite useful in lots of situations. To resolve a fingerprint you need a context to resolve it on. The context contains classes and thus methods to which the fingerprint can be resolved against. Example: _You have a fingerprint which you manually want to resolve **without** the help of the patcher._

> **Note**: A fingerprint should not be added to the constructor of `BytecodePatch` if manual resolution is intended, because the patcher would try resolve it before manual resolution.

- On a **list of classes** using [`MethodFingerprint.resolve`](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/fingerprint/method/impl/MethodFingerprint.kt#L49)

  This can be useful, if a fingerprint should be resolved to a smaller subset of classes, otherwise the fingerprint can be resolved by the patcher automatically.

  ```kt
  class DisableAdsPatch : BytecodePatch(
      /* listOf(LoadAdsFingerprint) */
  ) {
      override fun execute(context: BytecodeContext): PatchResult {
          val result = LoadAdsFingerprint.also { it.resolve(context, context.classes) }.result
              ?: return PatchResultError("LoadAdsFingerprint not found")

          // ...
      }
  }
  ```

- On a **single class** using [`MethodFingerprint.resolve`](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/fingerprint/method/impl/MethodFingerprint.kt#L63)

  Sometimes you know a class but you need certain methods. In such case, you can resolve fingerprints on a class.

  ```kt
   class DisableAdsPatch : BytecodePatch(
      listOf(LoadAdsFingerprint)
  ) {
      override fun execute(context: BytecodeContext): PatchResult {
          val adsLoaderClass = context.classes.single { it.name == "Lcom/some/app/ads/Loader;" }

          val result = LoadAdsFingerprint.also { it.resolve(context, adsLoaderClass) }.result
              ?: return PatchResultError("LoadAdsFingerprint not found")

          // ...
      }
  }
  ```

- On a **method** using [`MethodFingerprint.resolve`](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/fingerprint/method/impl/MethodFingerprint.kt#L78)

  Resolving a fingerprint on a method is mostly only useful if the fingerprint is used to resolve certain information about a method such as `MethodFingerprintResult.scanResult`. Example: _A fingerprint should be used to resolve the method which loads ads. For that the fingerprint is added to the constructor of `BytecodePatch`. An additional fingerprint is responsible for finding the indices of the instructions with certain string references in the implementation of the method the first fingerprint resolved to._

  ```kt
  class DisableAdsPatch : BytecodePatch(
      /* listOf(LoadAdsFingerprint) */
  ) {
      override fun execute(context: BytecodeContext): PatchResult {
          // Make sure this fingerprint succeeds as the result is required
          val adsFingerprintResult = LoadAdsFingerprint.result
              ?: return PatchResultError("LoadAdsFingerprint not found")

          // Additional fingerprint to get the indices of two strings
          val proStringsFingerprint = object : MethodFingerprint(
              strings = listOf("free", "trial")
          ) {}

          proStringsFingerprint.also {
              // Resolve the fingerprint on the first fingerprints method
              it.resolve(context, adsFingerprintResult.method)
          }.result?.let { result ->
              // Use the fingerprints result
              result.scanResult.stringsScanResult!!.matches.forEach { match ->
                      println("The index of the string '${match.string}' is {match.index}")
                  }

          } ?: return PatchResultError("LoadAdsFingerprint not found")

          return PatchResultSuccess
      }
  }
  ```

## 🎯 The result of a fingerprint

After a `MethodFingerprint` resolves successfully, its result can be used. The result contains mutable and immutable references to the method and the class it is defined in.

> **Warning**: By default the immutable references **should be used** to prevent a mutable copy of the immutable references. For a patch to properly use a fingerprint though, usually write access is required. For that the mutable references can be used.

Among them, the result also contains [MethodFingerprintResult.scanResult](https://github.com/revanced/revanced-patcher/blob/main/src/main/kotlin/app/revanced/patcher/fingerprint/method/impl/MethodFingerprint.kt#L239) which contains additional useful properties:

```kt
    data class MethodFingerprintScanResult(
        val patternScanResult: PatternScanResult?,
        val stringsScanResult: StringsScanResult?
    ) {
       data class PatternScanResult(
            val startIndex: Int,
            val endIndex: Int,
            var warnings: List<Warning>? = null
        )

        data class StringsScanResult(val matches: List<StringMatch>){
            data class StringMatch(val string: String, val index: Int)
        }

        // ...
    }
```

The following properties are utilized by bytecode patches:

- The `MethodFingerprint.strings` allows patches to know the indices of the instructions which hold references to the strings.

- If a fingerprint defines `MethodFingerprint.opcodes`, the start and end index of the first instructions matching that pattern will be available. These are useful to patch the implementation of methods relative to the pattern. Ideally the pattern contains the instructions opcodes pattern which is to be patched, in order to guarantee a successfull patch.

  > **Note**: Sometimes long patterns might be necessary, but the bigger the pattern list, the higher the chance it mutates if the app updates. For that reason the annotation `FuzzyPatternScanMethod` can be used on a fingerprint. The `FuzzyPatternScanMethod.threshold` will define, how many opcodes can remain unmatched. `PatternScanResult.warnings` can then be used if necessary, if it is necessary to know where pattern missmatches occured.

## ⭐ Useful code closely related to fingerprints

### 🧩 Patches

- [CommentsPatch](https://github.com/revanced/revanced-patches/blob/main/src/main/kotlin/app/revanced/patches/youtube/layout/comments/bytecode/patch/CommentsPatch.kt)
- [MusicVideoAdsPatch](https://github.com/revanced/revanced-patches/blob/main/src/main/kotlin/app/revanced/patches/music/ad/video/patch/MusicVideoAdsPatch.kt)

### Fingerprints

- [LoadVideoAdsFingerprint](https://github.com/revanced/revanced-patches/blob/main/src/main/kotlin/app/revanced/patches/youtube/ad/video/fingerprints/LoadVideoAdsFingerprint.kt)
- [SeekbarTappingParentFingerprint](https://github.com/revanced/revanced-patches/blob/main/src/main/kotlin/app/revanced/patches/youtube/interaction/seekbar/fingerprints/SeekbarTappingParentFingerprint.kt)
-

## ⏭️ Whats next

The next section will give a suggestion on coding conventions and on the file structure of a patch.

Continue: [📜 Patch file structure and conventions](4_structure_and_conventions.md)
