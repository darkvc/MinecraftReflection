package vc.dark.minecraft.reflection;

import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.mapper.Mapper;
import vc.dark.reflection.ReflectClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MinecraftReflectClass extends ReflectClass {

    protected ClassMap mappings;

    // This should only be called by mappings.
    public MinecraftReflectClass(Class<?> clazz, ClassMap map) {
        super(clazz);
        this.mappings = map;
    }

    private MinecraftReflectClass(Object e, MinecraftReflectClass existing) {
        super(e);
        this.mappings = existing.mappings;
    }

    public ClassMap getMappings() {
        return mappings;
    }

    /**
     * In order to gain consistent behavior, you have to define a mapper that your class relies upon.
     * eg, if you are querying a mojang mapping, you should pass "mojang" to the mapperName.
     */
    @Deprecated
    public MinecraftReflectClass(String name, boolean fuzzyNms) throws ClassNotFoundException {
        this(name, MinecraftReflection.getMapper(), fuzzyNms);
    }

    public MinecraftReflectClass(String name, String mapperName, boolean fuzzyNms) throws ClassNotFoundException {
        this(name, (mapperName != null ? MinecraftReflection.getMapper(mapperName) : MinecraftReflection.getMapper()), fuzzyNms);
    }

    private MinecraftReflectClass(String name, Mapper mapper, boolean fuzzyNms) throws ClassNotFoundException {
        ReflectClass reflectClass;
        if (!fuzzyNms) {
            reflectClass = mapper.getExactClass(name);
        } else {
            reflectClass = mapper.getClass(name);
        }
        if (reflectClass == null) {
            throw new ClassNotFoundException("Could not find class " + name);
        }
        this.className = reflectClass.className;
        this.classObject = reflectClass.classObject;
        try {
            MinecraftReflectClass mcReflect = (MinecraftReflectClass) reflectClass;
            this.mappings = mcReflect.mappings;
        } catch (ClassCastException ignored) {
        }
        this.instance = null;
    }

    public MinecraftReflectClass wrap(Object b) {
        return new MinecraftReflectClass(b, this);
    }

    public String findField(String name) {
        if (this.mappings == null) {
            return name;
        }
        // Make sure original method doesn't exist.
        String[] mapped = this.mappings.getFields(name);
        for (String value : mapped) {
            try {
                this.classObject.getDeclaredField(value);
                return value;
            } catch (NoSuchFieldException ignored) {
            }
        }
        return name;
    }

    public String findMethod(String name, Class<?>[] params) {
        if (this.mappings == null) {
            return name;
        }
        String[] mapped = this.mappings.getMethods(name);
        Method[] methods = this.classObject.getDeclaredMethods();
        boolean match = false;
        String retVal = null;
        for (String value : mapped) {
            if (params != null) {
                // Attempt to quickly resolve it:
                try {
                    this.classObject.getDeclaredMethod(value, params);
                    return value;
                } catch (NoSuchMethodException ignored) {
                }
            } else {
                for (Method m : methods) {
                    if (m.getName().equals(value)) {
                        if (match) {
                            throw new IllegalArgumentException("Found more than one match for " + name + " - cannot use fuzzy search.");
                        }
                        retVal = value;
                        match = true;
                    }
                }
            }
        }
        if (retVal != null) {
            return retVal;
        }
        return name;
    }

    @Override
    public Object declaredField(String name) {
        return super.declaredField(findField(name));
    }

    @Override
    public void setDeclaredField(String name, Object obj) {
        super.setDeclaredField(findField(name), obj);
    }

    @Override
    public void setFinalField(String name, Object obj) {
        super.setFinalField(findField(name), obj);
    }

    @Override
    public Object declaredMethod(String name, Class<?>[] args, Object... params) {
        return super.declaredMethod(findMethod(name, args), args, params);
    }

    @Override
    public Object field(String name) {
        return super.field(findField(name));
    }

    @Override
    public Object method(String name, Class<?>[] args, Object... params) {
        return super.method(findMethod(name, args), args, params);
    }

    @Override
    @Deprecated
    public Object methodSearch(Class<?>[] args, Object... params) {
        return super.methodSearch(args, params);
    }

    @Deprecated
    @Override
    public Object constructor(Class<?>[] args, Object... params) {
        return super.constructor(args, params);
    }

    @Deprecated
    @Override
    public Object assumeConstructor(int idx, Object... params) {
        return super.assumeConstructor(idx, params);
    }

    @Deprecated
    @Override
    public Object declaredConstructor(Class<?>[] args, Object... params) {
        return super.declaredConstructor(args, params);
    }


}
