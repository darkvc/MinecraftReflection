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

    private static File cacheLocation = new File("cached_mcreflect");
    private RuntimeMapper runtimeMapper = new RuntimeMapper();
    private Map<String, Boolean> visitedClasses = new HashMap<>();
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
        ClassMap dupe = (ClassMap) runtimeMapper.getDuplicateClasses().get(originalClass);
        ClassMap existing = (ClassMap) runtimeMapper.getClasses().get(originalClass);
        ClassMap obfExisting = (ClassMap) runtimeMapper.getClasses().get(obfuscatedClass);
        if (dupe != null && existing != null && obfExisting != null) {
            throw new RuntimeException("Not sure what to do here. " + originalClass + " -> " + obfuscatedClass + "["
            + Strings.join(dupe.getMappings(), ",") + "] " +  "["
                    + Strings.join(existing.getMappings(), ",") + "] ");
        }
        boolean justCreated = false;
        if (dupe == null && (existing == null || obfExisting == null)) {
            runtimeMapper.clazz(originalClass, obfuscatedClass);
            existing = (ClassMap) runtimeMapper.getClasses().get(originalClass);
            obfExisting = (ClassMap) runtimeMapper.getClasses().get(obfuscatedClass);
            justCreated = true;
        }
        try {
            dos.writeBytes("CL " + originalClass + " " + obfuscatedClass + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*if (dupe != null) {
            // Flag as dupe.
            try {
                dos.writeBytes("DU " + originalClass + " " + obfuscatedClass + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!justCreated) {
            try {
                dos.writeBytes("AL " + originalClass + " " + obfuscatedClass + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            visitedClasses.put(originalClass, true);
        } else {

        }*/
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
        ClassMap map = runtimeMapper.getExactClassMap(originalClass);
        ClassMap obfExisting = runtimeMapper.getExactClassMap(obfuscatedClass);
        if (map == null || obfExisting == null) {
            this.clazz(originalClass, obfuscatedClass);
            map = runtimeMapper.getExactClassMap(originalClass);
            obfExisting = runtimeMapper.getExactClassMap(obfuscatedClass);
        }

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
//                case "AL":
//                    // Alias/extra original mapping.
//                    break;
//                case "DU":
//                    // Duplicate mapping.
//                    break;
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
