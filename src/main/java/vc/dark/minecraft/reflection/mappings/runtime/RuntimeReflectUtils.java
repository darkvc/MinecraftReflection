package vc.dark.minecraft.reflection.mappings.runtime;

import joptsimple.internal.Strings;
import vc.dark.minecraft.reflection.MinecraftReflectClass;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.reflection.ReflectClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RuntimeReflectUtils {

    public static ReflectClass getReflectClass(ClassMap[] maps) throws ClassNotFoundException {
        if (maps.length < 1) {
            return null;
        }

        List<String> errorMap = new ArrayList<>();
        for (ClassMap map : maps) {
            errorMap.addAll(Arrays.asList(map.getMappings()));
//            System.out.println("Attempting: " + Arrays.toString(map.getMappings()));
            for (String mapping : map.getMappings()) {
                try {
                    Class<?> clazz = Class.forName(mapping);
                    if (clazz == null) {
                        throw new ClassNotFoundException("Could not find class " + mapping);
                    } else {
                        return new MinecraftReflectClass(clazz, map);
                    }
                } catch (ClassNotFoundException ignored) {
                }
            }
        }

        throw new ClassNotFoundException("Could not find any of these classes: ["
                + Strings.join(errorMap, ",") + "]");
    }
}
