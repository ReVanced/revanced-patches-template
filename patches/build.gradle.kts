group = "app.tristan"

patches {
    about {
        name = "ReVanced Patches Tristan"
        description = "Patches for ReVanced by tristan23612"
        source = "git@github.com:tristan23612/revanced-patches-tristan.git"
        author = "tristan23612"
        contact = "tristan23612@gmail.com"
        website = "https://github.com/tristan23612/revanced-patches-tristan"
        license = "GNU General Public License v3.0"
    }
}

dependencies {
    // Required due to smali, or build fails. Can be removed once smali is bumped.
    implementation(libs.guava)

    implementation(libs.apksig)

    // Android API stubs defined here.
    compileOnly(project(":patches:stub"))
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexplicit-backing-fields",
            "-Xcontext-parameters"
        )
    }
}

publishing {
    repositories {
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/tristan23612/revanced-patches-tristan")
            credentials(PasswordCredentials::class)
        }
    }
}
