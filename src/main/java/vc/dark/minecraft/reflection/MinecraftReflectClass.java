package vc.dark.minecraft.reflection;

import vc.dark.minecraft.reflection.mappings.ClassMap;
import vc.dark.minecraft.reflection.mappings.Mappings;
import vc.dark.reflection.ReflectClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MinecraftReflectClass extends ReflectClass {

    protected ClassMap mappings;
    private Map<String, String> alreadyFoundMethods = new HashMap<>();
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
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        this.instance = null;
    }

    @Deprecated
    public MinecraftReflectClass(String name) throws ClassNotFoundException {
        ReflectClass reflectClass = MinecraftReflection.getClass(name);
        this.className = reflectClass.className;
        this.classObject = reflectClass.classObject;
        try {
            MinecraftReflectClass legacy = (MinecraftReflectClass) reflectClass;
            this.mappings = legacy.mappings;
        } catch (ClassCastException e) {
            e.printStackTrace();
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
        if (name.length() < 4) {
            return name;
        }
        if (alreadyFoundFields.get(name) != null) {
            return alreadyFoundFields.get(name);
        }       // Make sure original method doesn't exist.
        String res = this.mappings.getField(name);
        // Test to make sure method exists.
        if (res == null) {
            // Pray and hope.
            return name;
        }
        // Check which variant
        for (Field m : this.classObject.getFields()) {
            if (m.getName().equals(name)) {
                // Original value exists.
                alreadyFoundFields.put(name, name);
                return res;
            }
            if (m.getName().equals(res)) {
                // Mapped value exists.
                alreadyFoundFields.put(name, res);
                return res;
            }
        }
        for (Field m : this.classObject.getDeclaredFields()) {
            if (m.getName().equals(name)) {
                // Original value exists.
                alreadyFoundFields.put(name, name);
                return res;
            }
            if (m.getName().equals(res)) {
                // Mapped value exists.
                alreadyFoundFields.put(name, res);
                return res;
            }
        }
        return name;
    }

    private String findMethod(String name) {
        if (this.mappings == null) {
            return name;
        }
        // Slight optimization
        if (name.length() < 4) {
            return name;
        }
        if (alreadyFoundMethods.get(name) != null) {
            return alreadyFoundMethods.get(name);
        }
        // Make sure original method doesn't exist.
        String res = this.mappings.getMethod(name);
        // Test to make sure method exists.
        if (res == null) {
            // Pray and hope.
            return name;
        }
        // Check which variant
        for (Method m : this.classObject.getMethods()) {
            if (m.getName().equals(name)) {
                // Original value exists.
                alreadyFoundMethods.put(name, name);
                return res;
            }
            if (m.getName().equals(res)) {
                // Mapped value exists.
                alreadyFoundMethods.put(name, res);
                return res;
            }
        }
        for (Method m : this.classObject.getDeclaredMethods()) {
            if (m.getName().equals(name)) {
                // Original value exists.
                alreadyFoundMethods.put(name, name);
                return res;
            }
            if (m.getName().equals(res)) {
                // Mapped value exists.
                alreadyFoundMethods.put(name, res);
                return res;
            }
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
        return super.declaredMethod(findMethod(name), args, params);
    }

    @Override
    public Object field(String name) {
        return super.field(findField(name));
    }

    @Override
    public Object fuzzyMethod(String name, Object... params) {
        return super.fuzzyMethod(findMethod(name), params);
    }

    @Override
    public Object method(String name, Class<?>[] args, Object... params) {
        return super.method(findMethod(name), args, params);
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
