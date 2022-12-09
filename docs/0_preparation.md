# 👶 Preparing a development environment

To develop ReVanced patches, a certain development environment is required.

## 📝 Prerequisites

- A Java IDE supporting Kotlin such as [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- Knowledge of Java, [Kotlin](https://kotlinlang.org) and [Dalvik bytecode](https://source.android.com/docs/core/runtime/dalvik-bytecode)
- Android reverse engineering tools such as [jadx](https://github.com/skylot/jadx)

## 🏃 Prepare the environment

For this guide, the official [ReVanced patches](https://github.com/revanced/revanced-patches) repository will be used as a base.

1. Clone the repository

   ```bash
   git clone https://github.com/revanced/revanced-patches && cd revanced-patches
   ```

2. Build the patches

   ```bash
   ./gradlew build
   ```

## ⏭️ Whats next

The following section will give you a basic understanding of the [ReVanced patcher](https://github.com/revanced/revanced-patcher).

Continue: [💉 Introduction to the ReVanced patcher](1_introduction.md)
