# Minecraft Reflection Library

This is a work-in-progress Minecraft Reflection
plugin/library that aids in making reflection calls to 
Minecraft 1.17 and above (with backwards compatible).

This plugin downloads the mappings from Mojang and Spigot and
builds a combined list of class and method/field mappings.

Because this library pulls both Spigot's and Mojang's mappings, there will be
class name mapping conflicts.  To fix this, Mojang mappings will take precedence over Spigot/Bukkit mappings, so you are
highly encouraged to use Mojang specific class/methods/fields names where it is possible.  

TODO: More readme writing.

## How to install

Repository:
```
<repository>
    <id>github-darkvc</id>
    <url>https://github.com/darkvc/MinecraftReflection/raw/maven/</url>
    <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
    </snapshots>
</repository>
```

Dependency:
```
<dependency>
  <groupId>vc.dark.minecraft</groupId>
  <artifactId>minecraftreflection</artifactId>
  <version>1.1-SNAPSHOT</version>
</dependency>
```

You do not need to install anything else (because the originally planned server plugin 
component of this has been scrapped).



