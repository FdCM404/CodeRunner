pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "CodeRunner"

include("contract", "manager-server", "run-server", "client")
