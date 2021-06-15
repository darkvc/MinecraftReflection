package vc.dark.minecraft.reflection.mappings.classmap;

import java.util.*;

public class EntryMap {

    private String original;
    private HashSet<String> obfuscated;

    protected EntryMap() {
        // For custom implementations that do *not* need LinkedHashMultiMap!
    }

    public EntryMap(String obfuscated, String original) {
        this.original = original;
        this.obfuscated = new HashSet<>();
        addObfuscated(obfuscated);
    }

    public void addObfuscated(String value) {
        if (this.obfuscated != null) {
            this.obfuscated.add(value);
        }
    }

    public String getOriginal() {
        return this.original;
    }

    public List<String> getObfuscated() {
        if (obfuscated == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(obfuscated);
    }

    public boolean hasObfuscated(String v) {
        if (obfuscated == null) {
            return false;
        }
        return obfuscated.contains(v);
    }

    public String[] getMappings() {
        if (obfuscated == null) {
            return new String[0];
        }
        // Prefer mojang obfuscation first.
        List<String> result = new ArrayList<>();
        result.add(original);
        result.addAll(obfuscated);
        return result.toArray(new String[0]);
    }

    public String toString() {
        return Arrays.toString(this.getMappings());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof EntryMap)) {
            return false;
        }
        return obj.toString().equals(this.toString());
    }
}
