import org.gradle.api.plugins.JavaPluginExtension

allprojects {
    group = "isel.coderunner"
    version = "1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        }
        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
        }
    }
}
