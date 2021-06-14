package vc.dark.minecraft.reflection.mappings;

import vc.dark.minecraft.reflection.MinecraftReflection;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.parser.*;
import vc.dark.minecraft.reflection.mappings.runtime.Cache;
import vc.dark.minecraft.reflection.mappings.runtime.RuntimeMapper;
import vc.dark.minecraft.reflection.mappings.runtime.RuntimeParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** @noinspection Duplicates*/
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
            InternetParser parser = new InternetParser(mojangUrl, new MojangParser());
            parser.parse(null, out);
            parser = new InternetParser(bukkitClassesUrl, new BukkitParser());
            parser.parse(null, bukkitOnly.wrap(out));
            parser = new InternetParser(bukkitMembersUrl, new BukkitParser());
            parser.parse(null, bukkitOnly.wrap(out));
        }

        return runtimeMap;
    }

    /*
    private static void loadMappingsOld(String version, String bukkitClassesUrl, String bukkitMembersUrl, String mojangUrl) {
        File cache = new File("cached_mcreflect");
        // Parse 1.17 mappings.
        File map;
        map = new File(cache, "combined-" + version);
        if (map.exists()) {
            // Load this instead.
            System.out.println("Loading combined mappings.");
            try {
                loadCombined(Files.readAllLines(map.toPath()).toArray(new String[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            map = new File(cache, "mojang-" + version);
            if (!map.exists()) {
                System.out.println("Downloading Mojang mappings.");
                // grab it.
                String data = getData(mojangUrl);
                try {
                    Files.write(map.toPath(), data.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Could not download mojang mappings!");
                }
            }
            map = new File(cache, "bukkit-classes-" + version);
            if (!map.exists()) {
                System.out.println("Downloading Bukkit class mappings.");
                // grab it.
                String data = getData(bukkitClassesUrl);
                try {
                    Files.write(map.toPath(), data.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Could not download bukkit class mappings!");
                }
            }
            map = new File(cache, "bukkit-members-" + version);
            if (!map.exists()) {
                System.out.println("Downloading Bukkit member mappings.");
                // grab it.
                String data = getData(bukkitMembersUrl);
                try {
                    Files.write(map.toPath(), data.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Could not download bukkit member mappings!");
                }
            }

            map = new File(cache, "mojang-" + version);
            try {
                System.out.println("Parsing Mojang mappings.");
                parseMojang(Files.readAllLines(map.toPath()).toArray(new String[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            map = new File(cache, "bukkit-classes-" + version);
            System.out.println("Parsing Bukkit class mappings.");
            try {
                parseBukkitClasses(Files.readAllLines(map.toPath()).toArray(new String[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            map = new File(cache, "bukkit-members-" + version);
            System.out.println("Parsing Bukkit member mappings.");
            try {
                parseBukkitMembers(Files.readAllLines(map.toPath()).toArray(new String[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Remove all the reverses - we're done parsing.
            reverseClasses.clear();

            // Now combine them.
            System.out.println("Combining mappings.");
            try {
                saveCombined(new File(cache, "combined-" + version));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
     */

    public static void loadMappingsVersion(String version) {
        if (hasMappings()) {
            return;
        }
        File cache = new File("cached_mcreflect");
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

    @Deprecated
    public static Class<?> getClass(String className) throws ClassNotFoundException {
        MinecraftReflection.loadMappings();
        if (mapper == null) {
            return null;
        }
        return mapper.getClass(className);
    }

    public static ClassMap getClassMap(String className) {
        MinecraftReflection.loadMappings();
        if (mapper == null) {
            return null;
        }
        return mapper.getClassMap(className);
    }

    @Deprecated
    public static Class<?> getExactClass(String className) throws ClassNotFoundException {
        MinecraftReflection.loadMappings();
        if (mapper == null) {
            return null;
        }
        return mapper.getExactClass(className);
    }


    public static ClassMap getExactClassMap(String className) {
        MinecraftReflection.loadMappings();
        if (mapper == null) {
            return null;
        }
        return mapper.getExactClassMap(className);
    }
}
