# 💉 Introduction to the [ReVanced patcher](https://github.com/revanced/revanced-patcher)

Familiarize yourself with the [ReVanced patcher](https://github.com/revanced/revanced-patcher).

## 📙 How it works

```kt
// Prepare patches to apply and files to merge

val patches = PatchBundle.Jar("revanced-patches.jar").loadPatches()
val mergeList = listOf("integrations.apk")

// Create the options for the patcher

val options = PatcherOptions(
     inputFile = File("some.apk"),
     resourceCacheDirectory = File("cache"),
)

// Create the patcher and add the prepared patches and files

val patcher = Patcher(options)
    .also { it.addPatches(patches) }
    .also { it.addFiles(mergeList) }

// Execute and save the patched files

patcher.executePatches().forEach { (patch, result) ->
    val log = if (!result.isSuccess)
        "failed"
    else
        "succeeded"
    println("$patch $log")
}

val result = patcher.save()
```

## ⏭️ Whats next

The next section will give you an understanding of a [ReVanced patch](https://github.com/revanced/revanced-patcher).

Continue: [🧩 Skeleton of a patch](2_skeleton.md)
