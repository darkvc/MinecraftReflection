package vc.dark.minecraft.reflection.mappings.runtime;

import jdk.internal.joptsimple.internal.Strings;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.parser.DataWriter;
import vc.dark.minecraft.reflection.mappings.parser.DataParser;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Cache implements DataWriter, DataParser {

    public static File cacheLocation = new File("cached_mcreflect");
    private RuntimeMapper runtimeMapper = new RuntimeMapper();
    private DataOutputStream dos;
    private File cacheFile;

    public Cache(String version) {
        cacheFile = new File(cacheLocation, "cached-" + version);
    }

    public void openFile() throws IOException {
        dos = new DataOutputStream(new FileOutputStream(cacheFile));
        dos.writeBytes("# Do not touch this file.\n");
    }

    @Override
    public void clazz(String originalClass, String obfuscatedClass) {
        runtimeMapper.clazz(originalClass, obfuscatedClass);
        try {
            dos.writeBytes("CL " + originalClass + " " + obfuscatedClass + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void field(String originalClass, String obfuscatedClass, String fieldOriginal, String fieldObfuscated) {
        checkClassExists(originalClass, obfuscatedClass);
        try {
            dos.writeBytes("FD " + originalClass + " " + obfuscatedClass + " " + fieldOriginal + " " + fieldObfuscated + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void method(String originalClass, String obfuscatedClass, String methodOriginal, String methodObfuscated) {
        checkClassExists(originalClass, obfuscatedClass);
        try {
            dos.writeBytes("MD " + originalClass + " " + obfuscatedClass + " " + methodOriginal + " " + methodObfuscated + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkClassExists(String originalClass, String obfuscatedClass) {
        if (runtimeMapper.getExactClassMaps(originalClass).length < 1)
            this.clazz(originalClass, obfuscatedClass);
    }

    public boolean cacheExists() {
        return cacheFile.exists();
    }

    @Override
    public void parse(String[] ignored, DataWriter out) {
        if (!cacheExists()) {
            throw new IllegalArgumentException("Could not find cache file!");
        }
        String[] lines = new String[0];
        try {
            lines = Files.readAllLines(cacheFile.toPath()).toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getCause());
        }
        for (String data : lines) {
            if (data.startsWith("#")) {
                continue;
            }
            String[] whitespace = data.split(" ");
            String type = whitespace[0];
            String originalClass = whitespace[1];
            String obfuscatedClass = whitespace[2];
            String key = "";
            String value = "";
            if (whitespace.length > 3) {
                key = whitespace[3];
                value = whitespace[4];
            }
            switch (type) {
                case "CL":
                    out.clazz(originalClass, obfuscatedClass);
                    break;
                case "MD":
                    if (whitespace.length <= 3) {
                        throw new IllegalArgumentException("Could not parse line " + data);
                    }
                    out.method(originalClass, obfuscatedClass, key, value);
                    break;
                case "FD":
                    if (whitespace.length <= 3) {
                        throw new IllegalArgumentException("Could not parse line " + data);
                    }
                    out.field(originalClass, obfuscatedClass, key, value);
                    break;
            }
        }
    }
}
