package vc.dark.minecraft.reflection;

import org.bukkit.Bukkit;
import vc.dark.minecraft.reflection.mappings.ClassMap;
import vc.dark.minecraft.reflection.mappings.Mappings;
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

    private static ReflectClass getClassObject(ClassMap map) {
        // Attempt to instantiate the correct object.
        MinecraftReflectClass instance;
        try {
            instance = new MinecraftReflectClass(map.getObfuscated(), map);
        } catch (ClassNotFoundException e) {
            // Try the other.
            try {
                instance = new MinecraftReflectClass(map.getOriginal(), map);
            } catch (ClassNotFoundException e2) {
                e.printStackTrace();
                e2.printStackTrace();
                return null;
            }
        }
        return instance;
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
            return null;
        }
    }

    public static ReflectClass getExactClass(String name) {
        return getReflectClass(name, Mappings.getExactClassName(name));
    }

    public static ReflectClass getClass(String name) {
        return getReflectClass(name, Mappings.getClassName(name));
    }

    private static ReflectClass getReflectClass(String name, ClassMap classMap) {
        if (name == null) {
            return null;
        }
        name = name.replace("net.minecraft.server.%s.", "");
        if (!Mappings.hasMappings() || classMap == null) {
            return new ReflectClass(getLegacyNmsClass("net.minecraft.server.%s." + name));
        }
        return getClassObject(classMap);
    }

    public static void main(String[] args) {
        // Simple CLI tool to save mappings.
        if (args.length < 1) {
            System.out.println("Usage: java -jar MinecraftReflection.jar [list|1.17]");
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
