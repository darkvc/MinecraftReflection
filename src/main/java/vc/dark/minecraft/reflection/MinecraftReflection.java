package vc.dark.minecraft.reflection;

import org.bukkit.Bukkit;
import vc.dark.minecraft.reflection.mappings.ClassMap;
import vc.dark.minecraft.reflection.mappings.Mappings;
import vc.dark.reflection.ReflectClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftReflection {

    public static void loadMappings(String versionMsg) {
        Mappings.loadMappingsVersion(versionMsg);
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
    @Deprecated
    private static Class<?> getLegacyClass(String nmsClass) {
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

    public static ReflectClass getClass(String name) {
        if (name == null) {
            return null;
        }
        name = name.replace("net.minecraft.server.%s.", "");
        if (!Mappings.hasMappings) {
            return new ReflectClass(getLegacyClass("net.minecraft.server.%s." + name));
        }
        ClassMap className = Mappings.getClassName(name);
        if (className != null) {
            return getClassObject(className);
        }
        return null;
    }
}
