package vc.dark.minecraft.reflection.mappings;

import vc.dark.minecraft.reflection.MinecraftReflection;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.config.Entry;
import vc.dark.minecraft.reflection.mappings.config.MappingConfiguration;
import vc.dark.minecraft.reflection.mappings.mapper.Mapper;
import vc.dark.minecraft.reflection.mappings.parser.*;
import vc.dark.minecraft.reflection.mappings.runtime.Cache;
import vc.dark.minecraft.reflection.mappings.mapper.RuntimeMapper;
import vc.dark.minecraft.reflection.mappings.runtime.ObfuscatedClassHelper;
import vc.dark.reflection.ReflectClass;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Mappings {

    private static final Map<String, MappingConfiguration> versions;

    static {
        versions = new HashMap<>();
           /*
            * If there are issues storing URLs inside of here.
            * Please create an issue on this repository
            *
            * https://github.com/darkvc/MinecraftReflection/issues
            */
        versions.put("1.17", new MappingConfiguration(
                new Entry("mojang", new Internet(Parsers.YARN, "https://launcher.mojang.com/v1/objects/84d80036e14bc5c7894a4fad9dd9f367d3000334/server.txt")),
                new Entry("bukkit",
                        new Internet(Parsers.CSRG,
                                "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-1.17-cl.csrg?at=3cec511b16ffa31cb414997a14be313716882e12"),
                        new Internet(Parsers.CSRG,
                                "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-1.17-members.csrg?at=3cec511b16ffa31cb414997a14be313716882e12"))
        ));

    }

    private static Mapper mapper;

    private static boolean hasMappings = false;

    public static boolean hasMappings() {
        return hasMappings;
    }

    public static String[] getSupportedVersions() {
        return versions.keySet().toArray(new String[0]);
    }

    private static Mapper loadMappings(String version) {
        for (Map.Entry<String, MappingConfiguration> entry : versions.entrySet()) {
            if (entry.getKey().endsWith(version)) {
                return entry.getValue().apply(version);
            }
        }
        return null;
    }

    public static void loadMappingsVersion(String version) {
        if (hasMappings()) {
            return;
        }
        File check = Cache.cacheLocation;
        if (!check.exists()) {
            if (!check.mkdir()) {
                throw new RuntimeException("Could not make " + check.getAbsolutePath() + " as cache directory!");
            }
        }
        mapper = loadMappings(version);
        hasMappings = mapper != null;
        if (!hasMappings) {
            System.err.println("Could not load mappings!");
        }
    }

    public static ReflectClass getClass(String className) throws ClassNotFoundException {
        MinecraftReflection.loadMappings();
        if (mapper == null) {
            return null;
        }
        return mapper.getClass(className);
    }

    public static ReflectClass getExactClass(String className) throws ClassNotFoundException {
        MinecraftReflection.loadMappings();
        if (mapper == null) {
            return null;
        }
        return mapper.getExactClass(className);
    }

    public static ClassMap[] getClassMaps(String className) {
        MinecraftReflection.loadMappings();
        if (mapper == null) {
            return new ClassMap[0];
        }
        return mapper.getClassMaps(className);
    }

    public static ClassMap[] getExactClassMaps(String className) {
        MinecraftReflection.loadMappings();
        if (mapper == null) {
            return new ClassMap[0];
        }
        return mapper.getExactClassMaps(className);
    }

    public static Mapper getMapper(String mapping) {
        MinecraftReflection.loadMappings();
        if (mapper == null) {
            return null;
        }
        return mapper.getMapper(mapping);
    }
}
