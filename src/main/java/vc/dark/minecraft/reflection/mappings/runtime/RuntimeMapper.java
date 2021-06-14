package vc.dark.minecraft.reflection.mappings.runtime;

import joptsimple.internal.Strings;
import vc.dark.minecraft.reflection.mappings.Mapper;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.classmap.EntryMap;
import vc.dark.minecraft.reflection.mappings.classmap.NestedEntryMap;
import vc.dark.minecraft.reflection.mappings.parser.DataWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeMapper extends NestedEntryMap implements Mapper, DataWriter {
    private Map<String, EntryMap> classes = new HashMap<>();
    private Map<String, EntryMap> duplicateClasses = new HashMap<>();

    public RuntimeMapper() {
        super(true);
    }

    public Map<String, EntryMap> getClasses() {
        return classes;
    }

    public Map<String, EntryMap> getDuplicateClasses() {
        return duplicateClasses;
    }

    @Override
    protected EntryMap constructNewEntry(String obfuscated, String original) {
        return new ClassMap(obfuscated, original);
    }

    private ClassMap resolveClassMap(String name) {
        ClassMap result = (ClassMap) duplicateClasses.get(name);
        if (result == null) {
            return (ClassMap) classes.get(name);
        }
        return result;
    }

    @Override
    public void clazz(String originalClass, String obfuscatedClass) {
        addMapping(originalClass, obfuscatedClass, classes, duplicateClasses);
    }

    @Override
    public void field(String originalClass, String obfuscatedClass, String fieldOriginal, String fieldObfuscated) {
        ClassMap map = getExactClassMap(originalClass);
        if (map == null) {
            clazz(originalClass, obfuscatedClass);
            map = getExactClassMap(originalClass);
        }
        map.addField(fieldOriginal, fieldObfuscated);
    }

    @Override
    public void method(String originalClass, String obfuscatedClass, String methodOriginal, String methodObfuscated) {
        ClassMap map = getExactClassMap(originalClass);
        if (map == null) {
            clazz(originalClass, obfuscatedClass);
            map = getExactClassMap(originalClass);
        }
        map.addMethod(methodOriginal, methodObfuscated);
    }

    @Override
    public Class<?> getClass(String className) throws ClassNotFoundException {
        return getExactClass(getClassMap(className));
    }

    @Override

    public Class<?> getExactClass(String className) throws ClassNotFoundException {
        return getExactClass(getExactClassMap(className));
    }

    @Override
    public ClassMap getClassMap(String partial) {
        String search = "." + partial.substring(0, 1).toUpperCase() + partial.substring(1);
        for (String k : duplicateClasses.keySet()) {
            if (k.endsWith(search) || k.equals(partial)) {
                return getExactClassMap(k);
            }
        }
        for (String k : classes.keySet()) {
            if (k.endsWith(search) || k.equals(partial)) {
                return getExactClassMap(k);
            }
        }
        return null;
    }

    @Override
    public ClassMap getExactClassMap(String className) {
        return resolveClassMap(className);
    }

    private Class<?> getExactClass(ClassMap map) throws ClassNotFoundException {
        if (map == null) {
            return null;
        }

        for (String mapping : map.getMappings()) {
            try {
                Class<?> clazz = Class.forName(mapping);
                if (clazz == null) {
                    throw new ClassNotFoundException("Could not find class " + mapping);
                } else {
                    return clazz;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        throw new ClassNotFoundException("Could not find classes in mapping: ["
                + Strings.join(map.getMappings(), ",") + "]");
    }
}
