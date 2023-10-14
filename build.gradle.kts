import org.gradle.kotlin.dsl.support.listFilesOrdered

plugins {
    kotlin("jvm") version "1.8.20"
    alias(libs.plugins.ksp)
    `maven-publish`
}

group = "app.revanced"

repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven { url = uri("https://jitpack.io") }
    // Required for FlexVer-Java
    maven {
        url = uri("https://repo.sleeping.town")
        content {
            includeGroup("com.unascribed")
        }
    }
}

dependencies {
    implementation(libs.revanced.patcher)
    implementation(libs.smali)
    implementation(libs.revanced.patch.annotation.processor)
    // TODO: Required because build fails without it. Find a way to remove this dependency.
    implementation(libs.guava)
    // Used in JsonGenerator.
    implementation(libs.gson)

    // A dependency to the Android library unfortunately fails the build, which is why this is required.
    compileOnly(project("dummy"))

    ksp(libs.revanced.patch.annotation.processor)
}

kotlin {
    jvmToolchain(11)
}

tasks {
    register<DefaultTask>("generateBundle") {
        description = "Generate dex files from build and bundle them in the jar file"

        dependsOn(build)

        doLast {
            val d8 = File(System.getenv("ANDROID_HOME")).resolve("build-tools")
                .listFilesOrdered().last().resolve("d8").absolutePath

            val artifacts = configurations.archives.get().allArtifacts.files.files.first().absolutePath
            val workingDirectory = layout.buildDirectory.dir("libs").get().asFile

            exec {
                workingDir = workingDirectory
                commandLine = listOf(d8, artifacts)
            }

            exec {
                workingDir = workingDirectory
                commandLine = listOf("zip", "-u", artifacts, "classes.dex")
            }
        }
    }

    register<JavaExec>("generateMeta") {
        description = "Generate metadata for this bundle"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("app.revanced.meta.PatchesFileGenerator")
    }

    // Required to run tasks because Gradle semantic-release plugin runs the publish task.
    // Tracking: https://github.com/KengoTODA/gradle-semantic-release-plugin/issues/435
    named("publish") {
        dependsOn("generateBundle")
        dependsOn("generateMeta")
    }
    register<JavaExec>("generateMergedStrings") {
        description = "Generate a merged English strings.xml file used for Crowdin translations"
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

publishing {
    publications {
        create<MavenPublication>("revanced-patches-publication") {
            from(components["java"])

            pom {
                name = "ReVanced Patches"
                description = "Patches for ReVanced."
                url = "https://revanced.app"

                licenses {
                    license {
                        name = "GNU General Public License v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                    }
                }
                developers {
                    developer {
                        id = "ReVanced"
                        name = "ReVanced"
                        email = "contact@revanced.app"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/revanced/revanced-patches.git"
                    developerConnection = "scm:git:git@github.com:revanced/revanced-patches.git"
                    url = "https://github.com/revanced/revanced-patches"
                }
            }
        }
    }
}