package net.redstonecraft.kotlinmcbuilder

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import net.redstonecraft.kotlinmcbuilder.version.VersionMetadata
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.security.MessageDigest
import java.util.*
import kotlin.math.min

object Utils {

    val kotlinVersionMetadata: VersionMetadata
        get() {
            return XmlMapper().readValue(get("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/maven-metadata.xml"), VersionMetadata::class.java)
        }

    val availableVersions: List<String>
        get() {
            return kotlinVersionMetadata.versioning.versions
        }

    val root = File("data")
    val dist = File(root, "dist")
    val build = File(root, "build")
    val mvnCmd = "apache-maven-3.8.3/bin/mvn" + if ("win" in System.getProperty("os.name").lowercase()) ".cmd" else ""

    fun generate(version: String) {
        val workDir = File(build, version)
        workDir.mkdirs()
        val pomXml = File(workDir, "pom.xml")
        pomXml.createNewFile()
        FileOutputStream(pomXml).use {
            it.write(generatePom(version).toByteArray(StandardCharsets.UTF_8))
        }
        val bungeeYml = File(workDir, "src/main/resources/bungee.yml")
        bungeeYml.parentFile.mkdirs()
        Files.copy(Utils::class.java.getResourceAsStream("/bungee.yml")!!, bungeeYml.toPath())
        val pluginYml = File(workDir, "src/main/resources/plugin.yml")
        Files.copy(Utils::class.java.getResourceAsStream("/plugin.yml")!!, pluginYml.toPath())
        val bungeeKt = File(workDir, "src/main/kotlin/net/redstonecraft/kotlinmc/bungee/KotlinBungee.kt")
        bungeeKt.parentFile.mkdirs()
        Files.copy(Utils::class.java.getResourceAsStream("/KotlinBungee.kt")!!, bungeeKt.toPath())
        val bukkitKt = File(workDir, "src/main/kotlin/net/redstonecraft/kotlinmc/bukkit/KotlinBukkit.kt")
        bukkitKt.parentFile.mkdirs()
        Files.copy(Utils::class.java.getResourceAsStream("/KotlinBukkit.kt")!!, bukkitKt.toPath())
        val log = File(workDir, "build.log")
        log.parentFile.mkdirs()
        log.createNewFile()
        FileOutputStream(log).use {
            val builder = ProcessBuilder(File(mvnCmd).absolutePath, "install")
            builder.redirectErrorStream(true)
            builder.directory(workDir)
            val process = builder.start()
            do {
                while (process.inputStream.available() > 0) {
                    val buffer = ByteArray(min(process.inputStream.available(), 1024))
                    process.inputStream.read(buffer)
                    it.write(buffer)
                }
            } while (process.isAlive)
            process.waitFor()
            it.write(("\nExit Code: " + process.exitValue() + "\n").toByteArray())
        }
        val outputjar = File(workDir, "target/kotlinmc-$version.jar")
        val target = File(dist, version)
        target.mkdirs()
        val filename = outputjar.nameWithoutExtension
        val finalJar = File(target, "$filename.jar")
        val finalPom = File(target, "$filename.pom")
        Files.copy(outputjar.toPath(), finalJar.toPath())
        Files.copy(pomXml.toPath(), finalPom.toPath())
        Files.copy(log.toPath(), File(target, "build.log").toPath())
        createHashes(finalPom)
        createHashes(finalJar)
        FileUtils.deleteDirectory(workDir)
    }

    private fun createHashes(file: File) {
        calcHash(file, "MD5", "md5")
        calcHash(file, "SHA-1", "sha1")
        calcHash(file, "SHA-256", "sha265")
        calcHash(file, "SHA-512", "sha512")
    }

    private fun calcHash(file: File, hash: String, hashFileEnding: String) {
        val alg = MessageDigest.getInstance(hash)
        FileInputStream(file).use {
            while (it.available() > 0) {
                val buffer = ByteArray(min(it.available(), 1024))
                it.read(buffer)
                alg.update(buffer)
            }
        }
        val out = File(file.parentFile, "${file.name}.$hashFileEnding")
        out.createNewFile()
        FileOutputStream(out).use {
            it.write(HexFormat.of().formatHex(alg.digest()).toByteArray())
        }
    }

    private fun generatePom(version: String): String = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>net.redstonecraft</groupId>
    <artifactId>kotlinmc</artifactId>
    <version>${version}</version>
    <packaging>jar</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <kotlin.code.style>official</kotlin.code.style>
        <kotlin.compiler.jvmTarget>1.8</kotlin.compiler.jvmTarget>
    </properties>

    <repositories>
        <repository>
            <id>bungeecord-repo</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        </repository>

        <repository>
            <id>spigot-repo</id>
            <url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>net.md-5</groupId>
            <artifactId>bungeecord-api</artifactId>
            <version>1.16-R0.5-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.bukkit</groupId>
            <artifactId>bukkit</artifactId>
            <version>1.8.8-R0.1-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib-jdk8</artifactId>
            <version>${"$"}{project.version}</version>
        </dependency>
    </dependencies>

    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${"$"}{project.version}</version>
                <executions>
                    <execution>
                        <id>compile</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>test-compile</id>
                        <phase>test-compile</phase>
                        <goals>
                            <goal>test-compile</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <sourceDirs>
                        <sourceDir>${"$"}{project.basedir}/src/main/kotlin</sourceDir>
                    </sourceDirs>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                    <appendAssemblyId>false</appendAssemblyId>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>
"""

    private fun get(url: String) = HttpClient.newBuilder().build().send(HttpRequest.newBuilder().uri(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString()).body()

}
