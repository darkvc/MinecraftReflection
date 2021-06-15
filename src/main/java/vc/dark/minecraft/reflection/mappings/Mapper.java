package vc.dark.minecraft.reflection.mappings;

import vc.dark.minecraft.reflection.MinecraftReflectClass;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;

public interface Mapper {
    MinecraftReflectClass getClass(String className) throws ClassNotFoundException;
    MinecraftReflectClass getExactClass(String className) throws ClassNotFoundException;
    ClassMap[] getClassMaps(String className);
    ClassMap[] getExactClassMaps(String className);
}
