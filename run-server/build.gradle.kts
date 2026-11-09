plugins {
    application
    id("com.gradleup.shadow") version "8.3.8"
}

dependencies {
    implementation(project(":contract"))
    implementation("io.grpc:grpc-netty-shaded:1.76.0")
    implementation("redis.clients:jedis:6.0.0")
    implementation("com.github.docker-java:docker-java-core:3.7.1")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.7.1")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.36")
}

application {
    mainClass = "coderunner.runserver.RunServer"
}

tasks.shadowJar {
    archiveClassifier = "all"
    manifest { attributes["Main-Class"] = application.mainClass.get() }
    mergeServiceFiles()
}
