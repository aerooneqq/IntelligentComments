import org.gradle.api.tasks.testing.logging.TestExceptionFormat

buildscript {
    repositories {
        maven { setUrl("https://cache-redirector.jetbrains.com/maven-central") }
    }

    dependencies {
        classpath("com.jetbrains.rd:rd-gen:2022.1.3")
    }
}

plugins {
    id("java")
    id("me.filippov.gradle.jvm.wrapper") version "0.10.0"
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    id("org.jetbrains.intellij") version "1.5.2"
    id("com.ullink.nuget") version "2.23"
}

apply {
    plugin("com.jetbrains.rdgen")
}

val riderProjectName: String by project
val intellijPluginId: String by project
val pluginVersion: String by project
val commonDll: String by project
val sdkVersion: String by project
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
    val dashIndex = sdkVersion.indexOf("-")
    if (dashIndex != -1) {
        return sdkVersion.substring(0, dashIndex) + ".0"
    }

    return "$sdkVersion.0"
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

val rdLibDirectory: () -> File = { file("${tasks.setupDependencies.get().idea.get().classes}/lib/rd") }
extra["rdLibDirectory"] = rdLibDirectory

val dotNetSrcDir = File(projectDir, "src/dotnet")

repositories {
    maven { setUrl("https://cache-redirector.jetbrains.com/maven-central") }
}

jvmWrapper {
    linuxJvmUrl = "https://cache-redirector.jetbrains.com/intellij-jbr/jbrsdk-11_0_11-linux-x64-b1341.60.tar.gz"
    macJvmUrl = "https://cache-redirector.jetbrains.com/intellij-jbr/jbrsdk-11_0_11-osx-x64-b1341.60.tar.gz"
    windowsJvmUrl = "https://cache-redirector.jetbrains.com/intellij-jbr/jbrsdk-11_0_11-windows-x64-b1341.60.tar.gz"
}

sourceSets {
    main {
        java.srcDir("src/rider/main/kotlin")
        resources.srcDir("src/rider/main/resources")
    }
    test {
        java.srcDir("test/kotlin")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    testImplementation("org.testng:testng:7.5")
}

apply(plugin = "com.jetbrains.rdgen")
configure<com.jetbrains.rd.generator.gradle.RdGenExtension> {
    val modelDir = file("$rootDir/protocol/src/main/kotlin/model")
    val csOutput = file("$rootDir/src/dotnet/IntelligentComments.Rider/Model")
    val ktOutput = file("$rootDir/src/rider/main/kotlin/com/intelligentcomments/model")

    verbose = true
    classpath({
        "${rdLibDirectory()}/rider-model.jar"
    })

    sources("$modelDir/rider")
    hashFolder = "$rootDir/build/rdgen/rider"
    packages = "model.rider"

    generator {
        language = "kotlin"
        transform = "asis"
        root = "com.jetbrains.rider.model.nova.ide.IdeRoot"
        directory = "$ktOutput"
    }

    generator {
        language = "csharp"
        transform = "reversed"
        namespace = "JetBrains.Rider.Model"
        root = "com.jetbrains.rider.model.nova.ide.IdeRoot"
        directory = "$csOutput"
    }
}

intellij {
    type.set("RD")
    version.set(sdkVersion)
    downloadSources.set(false)
}

tasks {
    wrapper {
        gradleVersion = theGradleVersion
        distributionType = Wrapper.DistributionType.ALL
        distributionUrl = "https://cache-redirector.jetbrains.com/services.gradle.org/distributions/gradle-${gradleVersion}-all.zip"
    }

    val rdgen by existing
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
        dependsOn(rdgen)
        dependsOn(writeDotnetPluginProps)
        doLast {
            exec {
                executable("dotnet")
                args("build", "-c", buildConfiguration)
            }
        }
    }

    compileKotlin {
        dependsOn(rdgen)
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

        version.set(pluginVersion)
        pluginId.set(intellijPluginId)
        pluginDescription.set(getPluginDescription())
    }

    runIde {
        dependsOn(buildPlugin)
        jvmArgs("-Xmx1500m")
    }

    test {
        useTestNG()
        testLogging {
            showStandardStreams = true
            exceptionFormat = TestExceptionFormat.FULL
        }

        environment["LOCAL_ENV_RUN"] = "true"
    }

    prepareSandbox {
        copyReSharperDllsToSandbox()
    }

    prepareTestingSandbox {
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