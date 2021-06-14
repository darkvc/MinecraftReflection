package vc.dark.minecraft.reflection;

import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.reflection.ReflectClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MinecraftReflectClass extends ReflectClass {

    protected ClassMap mappings;
    private Map<String, String> alreadyFoundFields = new HashMap<>();

    // This should only be called by mappings.
    MinecraftReflectClass(String className, ClassMap map) throws ClassNotFoundException {
        super(className);
        this.mappings = map;
    }

    private MinecraftReflectClass(Object e, MinecraftReflectClass existing) {
        super(e);
        this.mappings = existing.mappings;
    }

    public MinecraftReflectClass(String name, boolean fuzzyNms) throws ClassNotFoundException {
        ReflectClass reflectClass;
        if (!fuzzyNms) {
            reflectClass = MinecraftReflection.getExactClass(name);
        } else {
            reflectClass = MinecraftReflection.getClass(name);
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

    private String findField(String name) {
        if (this.mappings == null) {
            return name;
        }
        if (alreadyFoundFields.get(name) != null) {
            return alreadyFoundFields.get(name);
        }
        // Make sure original method doesn't exist.
        String[] mapped = this.mappings.getFields(name);
        for (String value : mapped) {
            try {
                this.classObject.getDeclaredField(value);
                alreadyFoundFields.put(name, value);
                return value;
            } catch (NoSuchFieldException ignored) {
            }
        }
        alreadyFoundFields.put(name, name);
        return name;
    }

    private String findMethod(String name, Class<?>[] params) {
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

    /**
     * @deprecated This is no longer a suitable approach and will likely throw a
     * UnsupportedException in the future.
     * Use methodSearch instead.
     */
    @Override
    @Deprecated
    public Object fuzzyMethod(String name, Object... params) {
        return super.fuzzyMethod(findMethod(name, null), params);
    }

    @Override
    public Object method(String name, Class<?>[] args, Object... params) {
        return super.method(findMethod(name, args), args, params);
    }

    @Override
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
