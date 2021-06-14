package vc.dark.minecraft.reflection.mappings.classmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntryMap {

    private List<String> obfuscated;
    private List<String> originals;

    protected EntryMap() {

    }

    public EntryMap(String obfuscated, String original) {
        this.obfuscated = new ArrayList<>();
        this.originals = new ArrayList<>();
        this.obfuscated.add(obfuscated);
        this.originals.add(original);
    }

    public void addOriginal(String newOriginal) {
        if (originals.contains(newOriginal)) {
            return;
        }
        originals.add(newOriginal);
    }

    public void addObfuscated(String newObf) {
        if (obfuscated.contains(newObf)) {
            return;
        }
        obfuscated.add(newObf);
    }

    public String[] getOriginals() {
        return originals.toArray(new String[0]);
    }

    public String[] getObfuscated() {
        return obfuscated.toArray(new String[0]);
    }

    public String[] getMappings() {
        List<String> result = new ArrayList<>();
        // Prefer mojang obfuscation first.
        result.addAll(obfuscated);
        result.addAll(originals);
        return result.toArray(new String[0]);
    }
}
