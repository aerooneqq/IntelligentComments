// Affects the repositories used to resolve the plugins { } block
pluginManagement {
    repositories {
        maven { setUrl("https://cache-redirector.jetbrains.com/plugins.gradle.org") }
    }
}

rootProject.name = "intelligentcomments"

include(":protocol")