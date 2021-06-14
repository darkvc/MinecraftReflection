package vc.dark.minecraft.reflection.examples;


import vc.dark.minecraft.reflection.MinecraftReflectClass;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class ReflectNBTCompressedStreamTools extends MinecraftReflectClass {
    public ReflectNBTCompressedStreamTools() throws ClassNotFoundException {
        super("NBTCompressedStreamTools", true);
        if (this.mappings == null) {
            // Backwards compatibility for 1.16.5 (and below potentially)
            this.mappings = new ClassMap("", "");
            this.mappings.addMethod("readCompressed", "a");
            this.mappings.addMethod("writeCompressed", "a");
        }
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
