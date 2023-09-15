plugins {
    kotlin("jvm") version "1.8.20"
}

group = "app.revanced"

repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven {
        url = uri("https://maven.pkg.github.com/revanced/revanced-patcher")
        credentials {
            username = project.findProperty("gpr.user") as? String ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as? String ?: System.getenv("GITHUB_TOKEN")
        }
    }
    // Required for FlexVer-Java
    maven {
        url = uri("https://repo.sleeping.town")
        content {
            includeGroup("com.unascribed")
        }
    }
}

dependencies {
    implementation("app.revanced:revanced-patcher:14.2.1")
    implementation("com.android.tools.smali:smali:3.0.3")
    // Required because build fails without it.
    // TODO: Find a way to remove this dependency.
    implementation("com.google.guava:guava:32.1.2-jre")
    // Used in JsonGenerator.
    implementation("com.google.code.gson:gson:2.10.1")
    // A dependency to the Android library unfortunately fails the build,
    // which is why this is required for the patch change-oauth-client-id.
    compileOnly(project("dummy"))
}

kotlin {
    jvmToolchain(11)
}

tasks {
    register<DefaultTask>("generateBundle") {
        description = "Generate dex files from build and bundle them in the jar file"
        dependsOn(build)

        doLast {
            val androidHome = System.getenv("ANDROID_HOME") ?: throw GradleException("ANDROID_HOME not found")
            val d8 = "${androidHome}/build-tools/33.0.1/d8"
            val input = configurations.archives.get().allArtifacts.files.files.first().absolutePath
            val work = layout.buildDirectory.dir("libs").get().asFile

            exec {
                workingDir = work
                commandLine = listOf(d8, input)
            }

            exec {
                workingDir = work
                commandLine = listOf("zip", "-u", input, "classes.dex")
            }
        }
    }
    register<JavaExec>("generateMeta") {
        description = "Generate metadata for this bundle"
        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("app.revanced.meta.PatchesFileGenerator")
    }
    // Dummy task to fix the Gradle semantic-release plugin.
    // Remove this if you forked it to support building only.
    // Tracking issue: https://github.com/KengoTODA/gradle-semantic-release-plugin/issues/435
    register<DefaultTask>("publish") {
        group = "publish"
        description = "Dummy task"
        dependsOn(named("generateBundle"), named("generateMeta"))
    }
    register<JavaExec>("generateMergedStrings") {
        description = "Generate a merged English strings.xml file, used for crowdin translations"
        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath
        mainClass = "app.revanced.util.resources.ResourceUtils"
        args = listOf(
            // YouTube
            "src/main/resources/youtube/settings/host/values/",
            "src/main/resources/youtube/settings/raw/strings.xml",
            // Twitch
            "src/main/resources/twitch/settings/host/values/",
            "src/main/resources/twitch/settings/raw/strings.xml"
        )
    }
}
