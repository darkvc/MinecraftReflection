package vc.dark.minecraft.reflection.mappings;

import vc.dark.minecraft.reflection.MinecraftReflection;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mappings {

    private static final Map<String, String[]> versions;

    static {
        versions = new HashMap<>();
        versions.put("1.17", new String[]{
                /*
                 * If there are issues storing URLs inside of here.
                 * Please create an issue on this repository
                 *
                 * https://github.com/darkvc/MinecraftReflection/issues
                */
                "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-1.17-cl.csrg?at=3cec511b16ffa31cb414997a14be313716882e12",
                "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-1.17-members.csrg?at=3cec511b16ffa31cb414997a14be313716882e12",
                "https://launcher.mojang.com/v1/objects/84d80036e14bc5c7894a4fad9dd9f367d3000334/server.txt"
        });
    }

    public static Map<String, ClassMap> classes = new HashMap<>();
    public static Map<String, String> reverseClasses = new HashMap<>();

    private static boolean hasMappings = false;

    public static boolean hasMappings() {
        return hasMappings;
    }

    public static String[] getSupportedVersions() {
        return versions.keySet().toArray(new String[0]);
    }

    public static ClassMap getExactClassName(String original) {
        MinecraftReflection.loadMappings();
        if (!hasMappings()) {
            return null;
        }
        return classes.get(original);
    }

    public static ClassMap getClassName(String partial) {
        MinecraftReflection.loadMappings();
        if (!hasMappings()) {
            return null;
        }
        for (String k : classes.keySet()) {
            if (k.endsWith("." + partial.substring(0, 1).toUpperCase() + partial.substring(1)) || k.equals(partial)) {
                return getExactClassName(k);
            }
        }
        return null;
    }

    private static void loadMappings(String version, String bukkitClassesUrl, String bukkitMembersUrl, String mojangUrl) {
        File cache = new File("cached_mcreflect");
        // Parse 1.17 mappings.
        File map;
        map = new File(cache, "combined-" + version);
        if (map.exists()) {
            // Load this instead.
            try {
                loadCombined(Files.readAllLines(map.toPath()).toArray(new String[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            map = new File(cache, "bukkit-classes-" + version);
            if (!map.exists()) {
                // grab it.
                String data = getData(bukkitClassesUrl);
                try {
                    Files.write(map.toPath(), data.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Could not download bukkit classes!");
                }
            }
            // parse bukkit-classes-1.17
            try {
                parseBukkitClasses(Files.readAllLines(map.toPath()).toArray(new String[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            map = new File(cache, "bukkit-members-" + version);
            if (!map.exists()) {
                // grab it.
                String data = getData(bukkitMembersUrl);
                try {
                    Files.write(map.toPath(), data.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Could not download bukkit classes!");
                }
            }
            // parse members-1.17
            try {
                parseBukkitMembers(Files.readAllLines(map.toPath()).toArray(new String[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            map = new File(cache, "mojang-" + version);
            if (!map.exists()) {
                // grab it.
                String data = getData(mojangUrl);
                try {
                    Files.write(map.toPath(), data.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Could not download bukkit classes!");
                }
            }
            // parse mojang
            try {
                parseMojang(Files.readAllLines(map.toPath()).toArray(new String[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Remove all the reverses - we're done parsing.
            reverseClasses.clear();

            // Now combine them.
            try {
                saveCombined(new File(cache, "combined-" + version));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadMappingsVersion(String version) {
        if (hasMappings()) {
            return;
        }
        File cache = new File("cached_mcreflect");
        if (!cache.exists()) {
            if (!cache.mkdir()) {
                throw new IllegalArgumentException("Could not make cache directory (cached_mcreflect)!");
            }
        }
        for (Map.Entry<String, String[]> entry : versions.entrySet()) {
            if (entry.getKey().endsWith(version)) {
                String[] value = entry.getValue();
                loadMappings(entry.getKey(),
                       value[0], value[1], value[2]);
                break;
            }
        }
        hasMappings = classes.size() > 0;
        if (!hasMappings) {
            System.out.println("Could not load mappings.  ");
        }
    }

    private static void loadCombined(String[] lines) {
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
                    classes.put(originalClass, new ClassMap(originalClass, obfuscatedClass));
                    break;
                case "MD":
                    ClassMap target = classes.get(originalClass);
                    if (target == null) {
                        throw new IllegalArgumentException("Could not find class " + originalClass);
                    }
                    target.addMethod(key, value);
                    break;
                case "FD":
                    ClassMap target2 = classes.get(originalClass);
                    if (target2 == null) {
                        throw new IllegalArgumentException("Could not find class " + originalClass);
                    }
                    target2.addField(key, value);
                    break;
            }
        }
    }

    private static void saveCombined(File file) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
        dos.writeBytes("# Do not touch this file.\n");
        for (ClassMap map : Mappings.classes.values()) {
            dos.writeBytes("CL " + map.getOriginal() + " " + map.getObfuscated() + "\n");
            for (Map.Entry<String, String> m : map.getMethods().entrySet()) {
                dos.writeBytes("MD " + map.getOriginal() + " " + map.getObfuscated() + " " + m.getKey() + " " + m.getValue() + "\n");
            }
            for (Map.Entry<String, String> f : map.getFields().entrySet()) {
                dos.writeBytes("FD " + map.getOriginal() + " " + map.getObfuscated() + " " + f.getKey() + " " + f.getValue() + "\n");
            }
        }
    }

    private static void parseBukkitClasses(String[] lines) {
        Pattern bukkitClassPattern = Pattern.compile("([a-zA-Z0-9/$]+) ([a-zA-Z0-9/$]+)", Pattern.MULTILINE);
        for (String data : lines) {
            if (data.startsWith("#")) {
                continue;
            }
            Matcher m = bukkitClassPattern.matcher(data);
            while (m.find()) {
                String original = m.group(2).replace("/", ".");
                if (original.equals("package-info")) {
                    // Skip this.
                    continue;
                }
                String obfuscated = m.group(1).replace("/", ".");
                reverseClasses.put(obfuscated, original);
                classes.put(original, new ClassMap(original, obfuscated));
            }
        }
    }

    private static void parseBukkitMembers(String[] lines) {
        // Read data line by line
        for (String line : lines) {
            if (line.startsWith("#")) {
                continue;
            }
            // Split here
            String[] whitespace = line.split(" ");
            String className = whitespace[0].replace("/", ".");
            String original = whitespace[3];
            String obfuscated = whitespace[1];
            //System.out.println("Adding " + className + " method " + original + " -> " + obfuscated);
            ClassMap currentClass = classes.get(className);
            // Add class mapping.
            if (currentClass == null) {
                throw new IllegalArgumentException("Could not find class " + className + " while parsing " + line);
//                // Add class
//                classes.put(className, new ClassMap(className, obfuscated));
//                reverseClasses.put(obfuscated, className);
//                currentClass = classes.get(className);
            }
            currentClass.addMethod(original, obfuscated);
        }
    }

    private static void parseMojang(String[] lines) {
        Pattern mojangClassPattern = Pattern.compile("([a-z0-9A-Z\\-\\._$]+) -> ([a-z0-9A-Z\\-\\._$]+):", Pattern.MULTILINE);
        Pattern mojangFieldPattern = Pattern.compile("([a-zA-Z0-9_$]+) -> ([$a-zA-Z0-9_]+)", Pattern.MULTILINE);
        Pattern mojangMethodPattern = Pattern.compile("([<>a-zA-Z0-9_$]+)[\\(\\)].+ -> ([_a-zA-Z0-9$]+)", Pattern.MULTILINE);

        // Read data line by line
        ClassMap currentClass = null;
        for (String line : lines) {
            if (line.startsWith("#")) {
                continue;
            }
            // Check class
            Matcher classMatcher = mojangClassPattern.matcher(line);
            Matcher fieldMatcher = mojangFieldPattern.matcher(line);
            Matcher methodMatcher = mojangMethodPattern.matcher(line);
            if (classMatcher.find()) {
                // Check if bukkit renamed this class.
                String className = classMatcher.group(1);
                boolean exists = false;
                if (reverseClasses.containsKey(classMatcher.group(2))) {
                    // Okay then use that.
                    className = reverseClasses.get(classMatcher.group(2));
                    exists = true;
                }
                currentClass = classes.get(className);
                if (className.equals("package-info")) {
                    // Skip this.
                    continue;
                }
                // Add class mapping.
                if (currentClass == null) {
                    // Add class
                    classes.put(className, new ClassMap(className, classMatcher.group(2)));
                    if (!exists) {
                        reverseClasses.put(classMatcher.group(2), className);
                    }
                    currentClass = classes.get(classMatcher.group(1));
                }

                //System.out.println("Using " + currentClass.original);
            } else if (currentClass == null) {
                throw new IllegalArgumentException("Could not find current class while parsing " + line);
            } else if (fieldMatcher.find()) {
                String original = fieldMatcher.group(1);
                String obfuscated = fieldMatcher.group(2);
                if (original.equals(obfuscated)) {
                    // Ignore java lang overrides
                    continue;
                }
                currentClass.addField(original, obfuscated);
            } else if (methodMatcher.find()) {
                String original = methodMatcher.group(1);
                String obfuscated = methodMatcher.group(2);
                if (original.equals(obfuscated)) {
                    // Ignore java lang overrides
                    continue;
                }
                currentClass.addMethod(original, obfuscated);
            }
        }
    }


    private static String getData(String url2) {
        URL url = null;
        try {
            url = new URL(url2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = url.openStream();
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        } catch (IOException e) {
            System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
            e.printStackTrace();
            // Perform any other exception handling that's appropriate.
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new String(baos.toByteArray());
    }
}
