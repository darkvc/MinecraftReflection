# Minecraft Reflection Library

This is a work-in-progress Minecraft Reflection
plugin/library that aids in making reflection calls to 
Minecraft 1.17 and above (with backwards compatible).

This plugin downloads the mappings from Mojang and Spigot and
builds a combined list of class and method/field mappings.

Because this library pulls both Spigot's and Mojang's mappings, there will be
class name mapping conflicts.  To fix this, duplicate mappings are stored together, and then 
each duplicate class/method/field will be tested.

Note: Not all classes defined will be accessible at runtime, as some class names may have had
their names changed, and in some _cases_, class names are changed like this: `n.m.s.EULA` -> `n.m.s.Eula`. 

TODO: More readme writing.

## How to install

Repository:
```
<repository>
    <id>darkvc-repo</id>
    <url>https://repo.dark.vc/repository/public/</url>
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
  <artifactId>minecraft-reflection</artifactId>
  <version>1.1-SNAPSHOT</version>
</dependency>
```

You do not need to install anything else (because the originally planned server plugin 
component of this has been scrapped).



