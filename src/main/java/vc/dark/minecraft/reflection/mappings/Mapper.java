package vc.dark.minecraft.reflection.mappings;

import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;

public interface Mapper {
    @Deprecated
    Class<?> getClass(String className) throws ClassNotFoundException;
    ClassMap getClassMap(String className);
    @Deprecated
    Class<?> getExactClass(String className) throws ClassNotFoundException;
    ClassMap getExactClassMap(String className);
}
