package vc.dark.minecraft.reflection.mappings.classmap;

import com.google.common.collect.LinkedHashMultimap;
import joptsimple.internal.Strings;

import java.util.*;

public abstract class NestedEntryMap extends EntryMap {

    protected NestedEntryMap() {
    }

    public NestedEntryMap(String obfuscated, String original) {
        super(obfuscated, original);
    }

    protected EntryMap constructNewEntry(String obfuscated, String original) {
        return new EntryMap(obfuscated, original);
    }

    protected void addMapping(String original, String obfuscated, LinkedHashMultimap<String, EntryMap> mappings,
                            boolean batchDupes) {
        if (mappings.containsKey(original)) {
            if (batchDupes) {
                for (EntryMap existing : mappings.get(original)) {
                    if (!existing.hasObfuscated(obfuscated)) {
                        existing.addObfuscated(obfuscated);
                    }
                }
            } else {
                for (EntryMap existing : mappings.get(original)) {
                    if (existing.hasObfuscated(obfuscated)) {
                        return;
                    }
                }
                mappings.put(original, constructNewEntry(obfuscated, original));
            }
        }
        else {
            mappings.put(original, constructNewEntry(obfuscated, original));
        }
    }

    protected String[] getMappings(String original, LinkedHashMultimap<String, EntryMap> mappings) {
        List<String> origmap = new ArrayList<>();
        List<String> obfmap = new ArrayList<>();
        if (mappings.containsKey(original)) {
            for (EntryMap entry : mappings.get(original)) {
                origmap.add(entry.getOriginal());
                obfmap.addAll(entry.getObfuscated());
            }
            obfmap.addAll(origmap);
            return obfmap.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }
}
