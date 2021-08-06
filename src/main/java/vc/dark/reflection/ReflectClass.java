package vc.dark.reflection;

import java.lang.reflect.*;
import java.util.Arrays;

public class ReflectClass {
    public String className;
    public Class<?> classObject;
    public Object instance;

    public ReflectClass() {
        // For custom implementations.
    }

    public ReflectClass(Class<?> instance) {
        this.className = instance.getName();
        this.classObject = instance;
        this.instance = null;
    }

    public ReflectClass(Object instance) {
        this.className = instance.getClass().getName();
        this.classObject = instance.getClass();
        this.instance = instance;
    }

    public ReflectClass(String path) throws ClassNotFoundException {
        this.classObject = Class.forName(path);
        this.className = this.classObject.getName();
        this.instance = null;
    }

    public Class getTargetClass() {
        return this.classObject;
    }

    public String getClassName() { return this.className; }

    public void setInstance(Object newInstance) {
        this.instance = newInstance;
    }

    @Deprecated
    public Object declaredField(String name) {
        try {
            Field f = this.classObject.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(this.instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public void setDeclaredField(String name, Object obj) {
        try {
            Field f = this.classObject.getDeclaredField(name);
            f.setAccessible(true);
            f.set(this.instance, obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
            return;
        }
    }

    @Deprecated
    public void setFinalField(String name, Object obj) {
        try {
            Field f = this.classObject.getDeclaredField(name);
            f.setAccessible(true);
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            } catch (Throwable e) {
                ReflectFinalFieldHelper.makeNonFinal(f);
            }
            f.set(this.instance, obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
    }

    @Deprecated
    public Object declaredMethod(String name, Class<?>[] args, Object... params) {
        try {
            Method f = this.classObject.getDeclaredMethod(name, args);
            f.setAccessible(true);
            return f.invoke(this.instance, params);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            e.printStackTrace();
            return null;
        }
    }

    public Object field(String name) {
        try {
            Field f = this.classObject.getField(name);
            return f.get(this.instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
            return null;
        }
    }

    public Field[] getFields() {
        return this.classObject.getFields();
    }

    public Method[] getMethods() {
        return this.classObject.getMethods();
    }

    public Field[] getDeclaredFields() {
        return this.classObject.getDeclaredFields();
    }

    public Method[] getDeclaredMethods() {
        return this.classObject.getDeclaredMethods();
    }

    /**
     * @deprecated Use method(), same parameters.  This will be removed in the future.
     */
    @Deprecated
    public Object fuzzyMethod(String name, Object... params) {
        return this.method(name, params);
    }

    public Object method(String name, Object... params) {
        Class<?>[] args = new Class<?>[params.length];
        int i = 0;
        for (Object param : params) {
            args[i++] = param.getClass();
        }
        return this.method(name, args, params);
    }

    public Object method(String name, Class<?>[] args, Object... params) {
        try {
            Method f = this.classObject.getMethod(name, args);
            if (args.length == 0) {
                return f.invoke(this.instance);
            } else {
                return f.invoke(this.instance, params);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * @deprecated This is no longer a suitable approach and will likely throw a
     * UnsupportedException in the future.
     * Use method() instead.
     */
    @Deprecated
    public Object methodSearch(Class<?>[] args, Object... params) {
        String name = "";
        try {
            int c = 0;
            Method found = null;
            for (Method m : this.classObject.getMethods()) {
                if (m.getParameterCount() == args.length &&
                        Arrays.equals(m.getParameterTypes(), args))
                    if (c > 0) {
                        // More than one found, can't use this.
                        throw new NoSuchMethodException("More than one matched for " + name);
                    }
                found = m;
                c++;
            }
            if (found == null) {
                throw new NoSuchMethodException("Could not find method " + name);
            }
            return found.invoke(this.instance, params);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Object constructor(Class<?>[] args, Object... params) {
        try {
            Constructor f = this.classObject.getConstructor(args);
            return f.newInstance(params);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object assumeConstructor(int idx, Object... params) {
        try {
            Constructor f = this.classObject.getConstructors()[idx];
            return f.newInstance(params);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public Object declaredConstructor(Class<?>[] args, Object... params) {
        try {
            Constructor f = this.classObject.getDeclaredConstructor(args);
            f.setAccessible(true);
            return f.newInstance(params);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

}

