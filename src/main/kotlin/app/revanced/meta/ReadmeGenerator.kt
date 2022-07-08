package app.revanced.patches.meta

import java.io.File
import kotlin.io.writeText
import kotlin.collections.first
import app.revanced.patcher.util.patch.implementation.JarPatchBundle
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.extensions.PatchExtensions.description

class ReadmeGenerator {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            //should be moved to a file?
            val generalReadme =
                """
            # revanced-patches

            A repository of patches for use with ReVanced tooling

            # List of patches
            """.trimIndent()

            val tableHeader =
                """
            |💊Patch|📜Description|🎯Target package|🏹Target package version|
            |:-----:|:-----------:|:--------------:|:----------------------:|
            """.trimIndent()

            val readmeFile = File("README.md")

            val bundle = JarPatchBundle("build/libs/revanced-patches-2.9.4.jar").loadPatches()

            val builder = StringBuilder()

            builder.appendLine(generalReadme)
            builder.appendLine(tableHeader)

            for (patch in bundle) {
                val humanName =
                    patch.patchName.split('-').map { it.replaceFirstChar { it.uppercase() } }.joinToString(" ")

                val compatiblePackage = patch.compatiblePackages?.first()
                val latestVersion = compatiblePackage?.versions?.maxByOrNull { it.replace(".", "").toInt() } ?: "all"

                builder.appendLine("|$humanName|${patch.description}|${compatiblePackage?.name}|$latestVersion|")
            }

            readmeFile.writeText(builder.toString())
        }
    }
}
