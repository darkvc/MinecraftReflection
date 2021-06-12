package vc.dark.minecraft.reflection.examples;

import vc.dark.minecraft.reflection.MinecraftReflectClass;

public class ReflectNBTTagCompound extends MinecraftReflectClass {
    public ReflectNBTTagCompound(Object instance) throws ClassNotFoundException {
        super("NBTTagCompound");
        this.setInstance(instance);
        if (this.instance == null) {
            throw new ClassNotFoundException("Could not set instance to NBTTagCompound reflection class.");
        }
    }

    public void setShort(String tag, short value) {
        this.method("setShort", new Class[]{String.class, short.class}, tag, value);
    }

    public void setFloat(String tag, float value) {
        this.method("setFloat", new Class[]{String.class, float.class}, tag, value);
    }

    public void setInt(String tag, int value) {
        this.method("setInt", new Class[]{String.class, int.class}, tag, value);
    }

    public short getShort(String tag) {
        return (short) this.method("getShort", new Class[]{String.class}, tag);
    }

    public float getFloat(String tag) {
        return (float) this.method("getFloat", new Class[]{String.class}, tag);
    }

    public int getInt(String tag) {
        return (int) this.method("getInt", new Class[]{String.class}, tag);
    }
}
