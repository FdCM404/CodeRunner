plugins {
    application
    id("com.gradleup.shadow") version "8.3.8"
}

dependencies {
    implementation(project(":contract"))
    implementation("io.grpc:grpc-netty-shaded:1.76.0")
}

application {
    mainClass = "coderunner.client.Client"
}

tasks.shadowJar {
    archiveClassifier = "all"
    manifest { attributes["Main-Class"] = application.mainClass.get() }
    mergeServiceFiles()
}
