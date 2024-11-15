import org.jetbrains.intellij.platform.gradle.tasks.PrepareSandboxTask
import org.jetbrains.changelog.exceptions.MissingVersionException
import org.jetbrains.intellij.platform.gradle.Constants
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import kotlin.io.path.absolute
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

plugins {
    alias(libs.plugins.changelog)
    alias(libs.plugins.gradleJvmWrapper)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.kotlinJvm)
}

allprojects {
    repositories {
        mavenCentral()
    }
}

repositories {
    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}

dependencies {
    intellijPlatform {
        rider(libs.versions.riderSdk, useInstaller = false)
        jetbrainsRuntime()
        instrumentationTools()

        bundledModule("intellij.rider")
    }

    testImplementation("org.testng:testng:7.5")
}

val riderProjectName: String by project
val intellijPluginId: String by project
val thePluginVersion: String by project
val commonDll: String by project
val ideaSdkVersion: String by project
val riderSdkVersion: String by project
val riderDll: String by project
val buildConfiguration: String by project
val jvmVersion: String by project
val theGradleVersion: String by project
val backendPluginId: String by project
val vendor: String by project

fun getPluginDescription(): String {
    return file("$rootDir/plugin_description.txt").readText().replace(Regex("(?s)\r?\n"), "<br />\n")
}

fun calculateVersionForPluginProps(): String {
    return riderSdkVersion
}

fun getAllDlls(): List<String> {
    val outputFolder = "${rootDir}/src/dotnet/${riderProjectName}/bin/${riderProjectName}/${buildConfiguration}"

    return listOf(
        "$outputFolder/${riderDll}.dll",
        "$outputFolder/${riderDll}.pdb",
        "$outputFolder/${commonDll}.dll",
        "$outputFolder/${commonDll}.pdb",
    )
}

fun AbstractCopyTask.copyReSharperDllsToSandbox() {
    val dllFiles = getAllDlls()

    for (f in dllFiles) {
        from(f) { into("${rootProject.name}/dotnet") }
    }

    doLast {
        for (f in dllFiles) {
            val file = file(f)
            if (!file.exists()) throw RuntimeException("File \"$file\" does not exist")
        }
    }
}

val dotNetSrcDir = File(projectDir, "src/dotnet")

repositories {
    maven { setUrl("https://cache-redirector.jetbrains.com/maven-central") }
}

sourceSets {
    main {
        kotlin.srcDir("src/rider/main/kotlin")
        resources.srcDir("src/rider/main/resources")
    }
    test {
        kotlin.srcDir("test/kotlin")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    wrapper {
        gradleVersion = theGradleVersion
        distributionType = Wrapper.DistributionType.ALL
        distributionUrl = "https://cache-redirector.jetbrains.com/services.gradle.org/distributions/gradle-${gradleVersion}-all.zip"
    }

    val rdGen = ":protocol:rdgen"

    val writeDotnetPluginProps by registering {
        val propsPath = file("$rootDir/src/dotnet/Plugin.props")
        var text = propsPath.readText()
        val match = Regex("<SdkVersion>.*</SdkVersion>").find(text)
        if (match != null) {
            text = text.replaceRange(match.range, "<SdkVersion>${calculateVersionForPluginProps()}</SdkVersion>")
        }

        propsPath.writeText(text)
    }

    val compileDotNet by registering {
        dependsOn(rdGen)
        dependsOn(writeDotnetPluginProps)
        doLast {
            exec {
                executable("dotnet")
                args("build", "-c", buildConfiguration)
            }
        }
    }

    compileKotlin {
        dependsOn(rdGen)
        kotlinOptions {
            jvmTarget = jvmVersion
        }
    }

    buildPlugin {
        dependsOn(compileDotNet)
        dependsOn(compileKotlin)
    }

    patchPluginXml {
        val matches = Regex("(?s)(.+?)(?=##|\$)").findAll(file("${rootDir}/CHANGELOG.md").readText())
        val text = StringBuilder()
        for (match in matches) {
            text.append(match.value)
        }

        val notes = text.replace(Regex("(?s)\r?\n"), "<br />\n")
        changeNotes.set(notes)

        pluginVersion.set(thePluginVersion)
        pluginId.set(intellijPluginId)
        pluginDescription.set(getPluginDescription())
        sinceBuild.set("242.20224.401")
    }

    runIde {
        dependsOn(buildPlugin)
        jvmArgs("-Xmx1500m")
    }

    withType<Test> {
        useTestNG()
        testLogging {
            showStandardStreams = true
        }

        environment["LOCAL_ENV_RUN"] = "true"
    }

    prepareSandbox {
        copyReSharperDllsToSandbox()
    }

    buildSearchableOptions {
        enabled = false
    }

    publishPlugin {
        dependsOn(buildPlugin)
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

val riderModel: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(riderModel.name, provider {
        intellijPlatform.platformPath.resolve("lib/rd/rider-model.jar").also {
            check(it.isRegularFile()) {
                "rider-model.jar is not found at \"$it\"."
            }
        }
    }) {
        builtBy(Constants.Tasks.INITIALIZE_INTELLIJ_PLATFORM_PLUGIN)
    }
}