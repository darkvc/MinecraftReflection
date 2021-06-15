package vc.dark.minecraft.reflection.mappings;

import vc.dark.minecraft.reflection.MinecraftReflectClass;
import vc.dark.minecraft.reflection.MinecraftReflection;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.parser.*;
import vc.dark.minecraft.reflection.mappings.runtime.Cache;
import vc.dark.minecraft.reflection.mappings.runtime.RuntimeMapper;
import vc.dark.minecraft.reflection.mappings.runtime.RuntimeParser;
import vc.dark.reflection.ReflectClass;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Mappings {

    private static final Map<String, String[]> versions;

    static {
        versions = new HashMap<>();
        versions.put("1.17", new String[]{
                /*
                 * If there are issues storing URLs inside of here.
                 * Please create an issue on this repository
                 *
                 * https://github.com/darkvc/MinecraftReflection/issues
                */
                "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-1.17-cl.csrg?at=3cec511b16ffa31cb414997a14be313716882e12",
                "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-1.17-members.csrg?at=3cec511b16ffa31cb414997a14be313716882e12",
                "https://launcher.mojang.com/v1/objects/84d80036e14bc5c7894a4fad9dd9f367d3000334/server.txt"
        });
    }

    private static Mapper mapper;

    private static boolean hasMappings = false;

    public static boolean hasMappings() {
        return hasMappings;
    }

    public static String[] getSupportedVersions() {
        return versions.keySet().toArray(new String[0]);
    }

    private static Mapper loadMappings(String version, String bukkitClassesUrl, String bukkitMembersUrl,
                                       String mojangUrl) {
        Cache newCache;
        RuntimeMapper runtimeMap = new RuntimeMapper();
        newCache = new Cache(version);
        DataWriter out = runtimeMap;
        if (newCache.cacheExists()) {
            newCache.parse(null, out);
        } else {
            try {
                newCache.openFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            out = new MultiWriter(runtimeMap, newCache);
            RuntimeParser bukkitOnly = new RuntimeParser(runtimeMap);
            InternetParser parser;
            parser = new InternetParser(bukkitClassesUrl, new BukkitParser());
            parser.parse(null, bukkitOnly.wrap(out));
            parser = new InternetParser(mojangUrl, new MojangParser());
            parser.parse(null, out);
            parser = new InternetParser(bukkitMembersUrl, new BukkitParser());
            parser.parse(null, bukkitOnly.wrap(out));
        }

        return runtimeMap;
    }

    public static void loadMappingsVersion(String version) {
        if (hasMappings()) {
            return;
        }
        File cache = Cache.cacheLocation;
        if (!cache.exists()) {
            if (!cache.mkdir()) {
                throw new IllegalArgumentException("Could not make cache directory (cached_mcreflect)!");
            }
        }
        for (Map.Entry<String, String[]> entry : versions.entrySet()) {
            if (entry.getKey().endsWith(version)) {
                String[] value = entry.getValue();
                mapper = loadMappings(entry.getKey(),
                        value[0], value[1], value[2]);
                break;
            }
        }
        hasMappings = mapper != null;
        if (!hasMappings) {
            System.out.println("Could not load mappings!");
        }
    }

    public static MinecraftReflectClass getClass(String className) throws ClassNotFoundException {
        MinecraftReflection.loadMappings();
        if (mapper == null) {
            return null;
        }
        return mapper.getClass(className);
    }

    public static MinecraftReflectClass getExactClass(String className) throws ClassNotFoundException {
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
}
