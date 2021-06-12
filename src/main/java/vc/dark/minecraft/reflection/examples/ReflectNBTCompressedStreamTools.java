package vc.dark.minecraft.reflection.examples;


import vc.dark.minecraft.reflection.MinecraftReflectClass;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class ReflectNBTCompressedStreamTools extends MinecraftReflectClass {
    public ReflectNBTCompressedStreamTools() throws ClassNotFoundException {
        super("NBTCompressedStreamTools");
    }

    public ReflectNBTTagCompound loadFile(File file) throws ClassNotFoundException {
        Object instance = this.method("readCompressed", new Class[]{File.class}, file);
        return new ReflectNBTTagCompound(instance);
    }

    public ReflectNBTTagCompound loadStream(InputStream input) throws ClassNotFoundException {
        Object instance = this.method("readCompressed", new Class[]{InputStream.class}, input);
        return new ReflectNBTTagCompound(instance);
    }

    public void saveFile(ReflectNBTTagCompound nbt, File file) {
        this.method("writeCompressed", new Class[]{nbt.instance.getClass(), File.class}, nbt.instance, file);
    }

    public void saveStream(ReflectNBTTagCompound nbt, OutputStream output) {
        this.method("writeCompressed", new Class[]{nbt.instance.getClass(), OutputStream.class}, nbt.instance, output);
    }
}
