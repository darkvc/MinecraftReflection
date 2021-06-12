# Minecraft Reflection Library

This is a work-in-progress Minecraft Reflection
plugin/library that aids in making reflection calls to 
Minecraft 1.17 and above (with backwards compatible).

This plugin downloads the mappings from Mojang and Spigot and
builds a combined list of class and method/field mappings.

TODO: More readme writing.

## How to install

Repository:
```
<repository>
    <id>github-darkvc</id>
    <url>https://maven.pkg.github.com/darkvc/*</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

Dependency:
```
<dependency>
  <groupId>vc.dark.minecraft</groupId>
  <artifactId>minecraftreflection</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

