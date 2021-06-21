package vc.dark.minecraft.reflection.mappings.mapper;

import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.reflection.ReflectClass;

public interface Mapper {
    ReflectClass getClass(String className) throws ClassNotFoundException;
    ReflectClass getExactClass(String className) throws ClassNotFoundException;
    ClassMap[] getClassMaps(String className);
    ClassMap[] getExactClassMaps(String className);

    // Only implemented specifically for a class, this just provides a convenience.
    Mapper getMapper(String mapping);
}
