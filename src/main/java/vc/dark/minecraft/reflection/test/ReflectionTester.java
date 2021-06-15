package vc.dark.minecraft.reflection.test;

import org.apache.commons.lang.exception.ExceptionUtils;
import vc.dark.minecraft.reflection.MinecraftReflectClass;
import vc.dark.minecraft.reflection.MinecraftReflection;
import vc.dark.minecraft.reflection.mappings.Mappings;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.parser.DataWriter;
import vc.dark.minecraft.reflection.mappings.runtime.Cache;
import vc.dark.reflection.ReflectClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionTester implements DataWriter {
    private int count = 0;

    private static List<String> ignored = new ArrayList<>();

    static {
        // The following classes listed may be removed from the JAR because it was optimized out.
        // Mojang's jar includes it, but there is no purpose because they are static final constants that
        // are in-lined into the code.
    }

    public void test() {
        try {
            Class.forName("org.bukkit.Bukkit");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("You must have a spigot jar in the classpath to run this test!");
        }

        Mappings.loadMappingsVersion("1.17");
        assert Mappings.hasMappings();

        Cache tester = new Cache("1.17");
        assert tester.cacheExists();
        tester.parse(null, this);
        System.out.println("Passed?");
    }

    @Override
    public void clazz(String originalClass, String obfuscatedClass) {
        if (ignored.contains(originalClass)) {
            return;
        }
        // Validate class exists.
        try {
            ReflectClass reflectClass = MinecraftReflection.getExactClass(originalClass);
            if (reflectClass == null || reflectClass.classObject == null) {
                System.out.println("Test failed: " + "Class: " + originalClass + " (" + obfuscatedClass + ")");
                return;
            }
            assert reflectClass != null && reflectClass.classObject != null;
            assert reflectClass instanceof MinecraftReflectClass && (reflectClass.getClassName().equals(originalClass) || reflectClass.getClassName().equals(obfuscatedClass));
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            /*assert Mappings.getExactClassMaps(originalClass).length > 0;
            String checkme = ExceptionUtils.getFullStackTrace(e.getCause()) + ExceptionUtils.getFullStackTrace(e);
            // only check class presence.
            if (!checkme.contains(originalClass) && (!checkme.contains("class " + obfuscatedClass) && !checkme.contains("at " + obfuscatedClass))) {
                System.out.println("Caught an exception: " + "Class: " + originalClass + " (" + obfuscatedClass + ")");
                e.printStackTrace();
                System.exit(255);
            }
            //e.printStackTrace();*/
        }
    }

    @Override
    public void field(String originalClass, String obfuscatedClass, String fieldOriginal, String fieldObfuscated) {
        if (ignored.contains(originalClass)) {
            return;
        }
        try {
            ReflectClass reflectClass = MinecraftReflection.getExactClass(originalClass);
            if (reflectClass == null || reflectClass.classObject == null) {
                System.out.println("Test failed: " + "Class: " + originalClass + " (" + obfuscatedClass + ") " + "Field" + " " + fieldOriginal + " (" + fieldObfuscated + ")");
                return;
            }
            assert reflectClass instanceof MinecraftReflectClass && (reflectClass.getClassName().equals(originalClass) || reflectClass.getClassName().equals(obfuscatedClass));
            boolean found = false;
            for (Field f : reflectClass.getFields()) {
                if (f.getName().equals(fieldOriginal) || f.getName().equals(fieldObfuscated)) {
                    found = true;
                    break;
                }
            }
            assert found;
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            /*ClassMap[] ents = Mappings.getExactClassMaps(originalClass);
            assert ents.length > 0;
            boolean found = false;
            for (ClassMap g : ents) {
                if (g.getFields(fieldOriginal).length > 0) {
                    found = true;
                    break;
                }
            }
            assert found;
            String checkme = ExceptionUtils.getFullStackTrace(e.getCause()) + ExceptionUtils.getFullStackTrace(e);
            // only check class presence.
            if (!checkme.contains(originalClass) && (!checkme.contains("class " + obfuscatedClass) && !checkme.contains("at " + obfuscatedClass))) {
                System.out.println("Caught an exception: " + "Class: " + originalClass + " (" + obfuscatedClass + ") " + "Field" + " " + fieldOriginal + " (" + fieldObfuscated + ")");
                e.printStackTrace();
                System.exit(255);
            }*/
            // ignore all errors like this?
        }
    }

    @Override
    public void method(String originalClass, String obfuscatedClass, String methodOriginal, String methodObfuscated) {
        if (ignored.contains(originalClass)) {
            return;
        }
        try {
            ReflectClass reflectClass = MinecraftReflection.getExactClass(originalClass);
            if (reflectClass == null || reflectClass.classObject == null) {
                System.out.println("Test failed: " + "Class: " + originalClass + " (" + obfuscatedClass + ") " + "Method" + " " + methodOriginal + " (" + methodObfuscated + ")");
                return;
            }
            assert reflectClass != null && reflectClass.classObject != null;
            assert reflectClass instanceof MinecraftReflectClass && (reflectClass.getClassName().equals(originalClass) || reflectClass.getClassName().equals(obfuscatedClass));
            boolean found = false;
            for (Method m : reflectClass.getMethods()) {
                if (m.getName().equals(methodOriginal) || m.getName().equals(methodObfuscated)) {
                    found = true;
                    break;
                }
            }
            assert found;
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            /*ClassMap[] ents = Mappings.getExactClassMaps(originalClass);
            assert ents.length > 0;
            boolean found = false;
            for (ClassMap g : ents) {
                if (g.getMethods(methodOriginal).length > 0) {
                    found = true;
                    break;
                }
            }
            assert found;
            String checkme = ExceptionUtils.getFullStackTrace(e.getCause()) + ExceptionUtils.getFullStackTrace(e);
            // only check class presence.
            if (!checkme.contains(originalClass) && (!checkme.contains("class " + obfuscatedClass) && !checkme.contains("at " + obfuscatedClass))) {
                System.out.println("Caught an exception: " + "Class: " + originalClass + " (" + obfuscatedClass + ") " + "Method" + " " + methodOriginal + " (" + methodObfuscated + ")");
                e.printStackTrace();
                System.exit(255);
            }
            //e.printStackTrace();*/
        }
    }
}
