plugins {
    id("java")
}

group = "org.example"

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-cli:commons-cli:1.9.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.release.set(23)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .map { zipTree(it) }
    })
}