package vc.dark.minecraft.reflection;

import org.bukkit.Bukkit;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.mapper.Mapper;
import vc.dark.minecraft.reflection.mappings.Mappings;
import vc.dark.minecraft.reflection.test.ReflectionTester;
import vc.dark.reflection.ReflectClass;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftReflection {

    private static boolean hasAttempted = false;

    public static void loadMappings(String versionMsg) {
        Mappings.loadMappingsVersion(versionMsg);
    }

    public static void loadMappings() {
        if (hasMappings()) {
            return;
        }
        if (!hasAttempted) {
            Mappings.loadMappingsVersion(Bukkit.getVersion());
            hasAttempted = true;
        }
    }

    public static boolean hasMappings() {
        return Mappings.hasMappings();
    }

    /* Uses regex and reflection to find all packages which contain the NMS version,
   then retrieves the class from the argument by the fully qualified name. */
    // Original derived from: https://gist.github.com/daviga404/7322545
    @Deprecated
    public static Class<?> getLegacyNmsClass(String nmsClass) {
        String version = null;
        Pattern pat = Pattern.compile("org\\.bukkit\\.craftbukkit\\.(v[\\d\\_R]+)");
        for (Package p : Package.getPackages()) {
            String name = p.getName();
            Matcher m = pat.matcher(name);
            if (m.matches()) {
                version = m.group(1);
                break;
            }
        }

        if (version == null) {
            Pattern pat2 = Pattern.compile("net\\.minecraft\\.server\\.(v[\\d\\_R]+)");
            for (Package p : Package.getPackages()) {
                String name = p.getName();
                Matcher m = pat2.matcher(name);
                if (m.matches()) {
                    version = m.group(1);
                    break;
                }
            }

            if (version == null) return null;
        }

        try {
            return Class.forName(String.format(nmsClass, version));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ReflectClass getExactClass(String name) {
        return getReflectClass(name, true);
    }

    public static ReflectClass getClass(String name) {
        return getReflectClass(name, false);
    }

    private static ReflectClass getReflectClass(String name, boolean exact) {
        if (name == null) {
            return null;
        }
        name = name.replace("net.minecraft.server.%s.", "");
        if (!Mappings.hasMappings()) {
            return new ReflectClass(getLegacyNmsClass("net.minecraft.server.%s." + name));
        }
        try {
            if (!exact) {
                return Mappings.getClass(name);
            } else {
                return Mappings.getExactClass(name);
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Mapper getMapper(String mapping) {
        if (!hasMappings()) {
            // Return the shim
            return getMapper();
        }
        return Mappings.getMapper(mapping);
    }

    // Argumentless mapper just points to this class.
    public static Mapper getMapper() {
        return new Mapper() {
            @Override
            public ReflectClass getClass(String className) throws ClassNotFoundException {
                return MinecraftReflection.getClass(className);
            }

            @Override
            public ReflectClass getExactClass(String className) throws ClassNotFoundException {
                return MinecraftReflection.getExactClass(className);
            }

            @Override
            public ClassMap[] getClassMaps(String className) {
                return Mappings.getClassMaps(className);
            }

            @Override
            public ClassMap[] getExactClassMaps(String className) {
                return Mappings.getExactClassMaps(className);
            }

            @Override
            public Mapper getMapper(String mapping) {
                return MinecraftReflection.getMapper(mapping);
            }
        };
    }

    public static void main(String[] args) {
        // Simple CLI tool to save mappings.
        if (args.length < 1) {
            System.out.println("Usage: java -jar MinecraftReflection.jar [list|1.17]");
            return;
        }
        if (args[0].equals("test")) {
            ReflectionTester tester = new ReflectionTester();
            tester.test();
            return;
        }
        if (args[0].equals("list")) {
            System.out.println("Supported versions: " + Arrays.toString(Mappings.getSupportedVersions()));
            return;
        }
        System.out.println("Got version " + args[0]);
        System.out.println("Attempting to load mappings...");
        loadMappings(args[0]);
        if (Mappings.hasMappings()) {
            System.out.println("Retrieved/combined mappings successfully!");
        }
    }
}
