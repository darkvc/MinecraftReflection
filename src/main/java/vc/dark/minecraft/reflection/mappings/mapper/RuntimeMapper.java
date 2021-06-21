package vc.dark.minecraft.reflection.mappings.mapper;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.classmap.EntryMap;
import vc.dark.minecraft.reflection.mappings.classmap.NestedEntryMap;
import vc.dark.minecraft.reflection.mappings.parser.DataWriter;
import vc.dark.minecraft.reflection.mappings.runtime.RuntimeReflectUtils;
import vc.dark.reflection.ReflectClass;

import java.util.*;

public class RuntimeMapper extends NestedEntryMap implements Mapper, DataWriter {
    private LinkedHashMultimap<String, EntryMap> classes = LinkedHashMultimap.create();

    public RuntimeMapper() {
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
    public ReflectClass getClass(String className) throws ClassNotFoundException {
        return RuntimeReflectUtils.getReflectClass(getClassMaps(className));
    }

    @Override
    public ReflectClass getExactClass(String className) throws ClassNotFoundException {
        return RuntimeReflectUtils.getReflectClass(getExactClassMaps(className));
    }

    @Override
    public ClassMap[] getClassMaps(String partial) {
        String search = "." + partial.substring(0, 1).toUpperCase() + partial.substring(1);
        for (String k : classes.keySet()) {
            if (k.endsWith(search) || k.equals(partial)) {
                return getExactClassMaps(k);
            }
        }
        return new ClassMap[0];
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

    @Override
    public Mapper getMapper(String mapping) {
        return this;
    }
}
