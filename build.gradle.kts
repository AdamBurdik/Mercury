subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    dependencies {
        "implementation"("org.jetbrains:annotations:26.0.2")

        "compileOnly"("org.projectlombok:lombok:1.18.36")
        "annotationProcessor"("org.projectlombok:lombok:1.18.36")
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}
