package vc.dark.minecraft.reflection.mappings.runtime;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import joptsimple.internal.Strings;
import vc.dark.minecraft.reflection.MinecraftReflectClass;
import vc.dark.minecraft.reflection.mappings.Mapper;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.classmap.EntryMap;
import vc.dark.minecraft.reflection.mappings.classmap.NestedEntryMap;
import vc.dark.minecraft.reflection.mappings.parser.DataWriter;

import java.util.*;

public class RuntimeMapper extends NestedEntryMap implements Mapper, DataWriter {
    private LinkedHashMultimap<String, EntryMap> classes = LinkedHashMultimap.create();

    public RuntimeMapper() {
        super(true);
    }

    public Map<String, Collection<EntryMap>> getClasses() {
        return Collections.unmodifiableMap(classes.asMap());
    }

    @Override
    protected EntryMap constructNewEntry(String obfuscated, String original) {
        return new ClassMap(obfuscated, original);
    }

    @Override
    public void clazz(String originalClass, String obfuscatedClass) {
        addMapping(originalClass, obfuscatedClass, classes, false);
    }

    @Override
    public void field(String originalClass, String obfuscatedClass, String fieldOriginal, String fieldObfuscated) {
        Preconditions.checkArgument(!obfuscatedClass.equals(""));
        ClassMap map = getExactObfClassMap(originalClass, obfuscatedClass);
        if (map == null) {
            clazz(originalClass, obfuscatedClass);
            map = getExactObfClassMap(originalClass, obfuscatedClass);
        }
        map.addField(fieldOriginal, fieldObfuscated);
    }

    @Override
    public void method(String originalClass, String obfuscatedClass, String methodOriginal, String methodObfuscated) {
        Preconditions.checkArgument(!obfuscatedClass.equals(""));
        ClassMap map = getExactObfClassMap(originalClass, obfuscatedClass);
        if (map == null) {
            clazz(originalClass, obfuscatedClass);
            map = getExactObfClassMap(originalClass, obfuscatedClass);
        }
        map.addMethod(methodOriginal, methodObfuscated);
    }

    @Override
    public MinecraftReflectClass getClass(String className) throws ClassNotFoundException {
        return getReflectClass(getClassMaps(className));
    }

    @Override
    public MinecraftReflectClass getExactClass(String className) throws ClassNotFoundException {
        return getReflectClass(getExactClassMaps(className));
    }

    private MinecraftReflectClass getReflectClass(ClassMap[] maps) throws ClassNotFoundException {
        if (maps.length < 1) {
            return null;
        }

        List<String> errorMap = new ArrayList<>();
        for (ClassMap map : maps) {
            errorMap.addAll(Arrays.asList(map.getMappings()));
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

    @Override
    public ClassMap[] getClassMaps(String partial) {
        String search = "." + partial.substring(0, 1).toUpperCase() + partial.substring(1);
        for (String k : classes.keySet()) {
            if (k.endsWith(search) || k.equals(partial)) {
                return getExactClassMaps(k);
            }
        }
        return null;
    }

    ClassMap getExactObfClassMap(String className, String obfuscated) {
        ClassMap[] entries = getExactClassMaps(className);
        for (ClassMap existing : entries) {
            if (existing.getObfuscated().contains(obfuscated)) {
                return existing;
            }
        }
        return null;
    }

    @Override
    public ClassMap[] getExactClassMaps(String className) {
        if (classes.containsKey(className)) {
            // Sigh.
            List<ClassMap> e = new ArrayList<>();
            for (EntryMap g : classes.get(className)) {
                e.add((ClassMap) g);
            }
            return e.toArray(new ClassMap[0]);
        }
        return new ClassMap[0];
    }
}
