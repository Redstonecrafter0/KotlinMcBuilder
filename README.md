# Kotlin MC Builder

This little project is a webserver that builds a
Bukkit/Bungeecord plugin with bundled Kotlin.

## Requirements
This project needs Java 17 to run.

## Setup
1. Download [Maven](https://dlcdn.apache.org/maven/maven-3/3.8.3/binaries/apache-maven-3.8.3-bin.zip).
2. Put the extracted directory next to the server.jar so that it can access it.
3. Run `java -jar server.jar <port>` where port is optional (defaults to 80).
4. Open [`http://localhost:<port>/`](https://localhost:80/) in your browser.

## Maven
To add the provided maven repo use this snippet.
```xml
<repositories>
    <repository>
        <id>kotlin-mc</id>
        <url>http://localhost/maven</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.redstonecraft</groupId>
        <artifactId>kotlinmc</artifactId>
        <version>${kotlin.version}</version>
    </dependency>
</dependencies>
```

## Use
The final plugin is a combined Bukkit and Bungeecord plugin, so you can use the same jar for both.  
The minimal Minecraft version for Bukkit is 1.8.8.  
The minimal Minecraft version for Bungeecord is 1.16.
The minimal Java version for the plugins is 8.
