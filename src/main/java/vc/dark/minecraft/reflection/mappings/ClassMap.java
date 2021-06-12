package vc.dark.minecraft.reflection.mappings;

import java.util.HashMap;
import java.util.Map;

public class ClassMap {

    private String original;
    private String obfuscated;
    private Map<String, String> methods = new HashMap<>();
    private Map<String, String> fields = new HashMap<>();


    public ClassMap(String original, String obfuscated) {
        this.original = original;
        this.obfuscated = obfuscated;
    }

    public String getOriginal() {
        return original;
    }

    public String getObfuscated() {
        return obfuscated;
    }

    public Map<String, String> getMethods() {
        return methods;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void addMethod(String original, String obfuscated) {
        this.methods.put(original, obfuscated);
    }

    public void addField(String original, String obfuscated) {
        this.fields.put(original, obfuscated);
    }

    // Note: We do not support reverse-mappings.

    public String getMethod(String original) {
        return this.methods.get(original);
    }

    public String getField(String original) {
        return this.fields.get(original);
    }
}
