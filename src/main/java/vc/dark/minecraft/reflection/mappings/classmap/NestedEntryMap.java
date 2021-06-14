package vc.dark.minecraft.reflection.mappings.classmap;

import joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class NestedEntryMap extends EntryMap {

    private boolean strictDupes = false;

    protected NestedEntryMap(boolean strictDupes) {
        this.strictDupes = strictDupes;
    }

    public NestedEntryMap(String obfuscated, String original) {
        super(obfuscated, original);
    }

    protected EntryMap constructNewEntry(String obfuscated, String original) {
        return new EntryMap(obfuscated, original);
    }

    protected void addMapping(String original, String obfuscated, Map<String, EntryMap> mappings,
                              Map<String, EntryMap> dupes) {
        EntryMap existing = mappings.get(original);
        EntryMap obfExisting = mappings.get(obfuscated);
        if (existing != null) {
            if (!Arrays.asList(existing.getObfuscated()).contains(obfuscated)) {
                if (dupes == null) {
                    throw new RuntimeException("Duplicate detected! Mapping " + original + " points to " + obfuscated + " " +
                            "but this mapping already exists: " + original + " -> " + Strings.join(existing.getObfuscated(), ","));
                } else {
                    // Add as a duplicate mapping.
                    if (strictDupes) {
                        if (dupes.get(original) != null) {
                            if (!Arrays.asList(dupes.get(original).getObfuscated()).contains(obfuscated)) {
                                throw new RuntimeException("Multiple Duplicates detected! Mapping " + original + " points to " + obfuscated + " " +
                                        "but this mapping already exists: " + original + " -> " + Strings.join(existing.getObfuscated(), ",")
                                        + " as well as this duplicate: " + original + " -> " +
                                        Strings.join(dupes.get(original).getObfuscated(), ","));
                            }
                            return;
                        }
                        if (obfExisting == null) {
                            mappings.put(obfuscated, constructNewEntry(obfuscated, original));
                            obfExisting = mappings.get(obfuscated);
                        } else {
                            obfExisting.addOriginal(original);
                            obfExisting.addObfuscated(obfuscated);
                        }
                        dupes.put(original, obfExisting);
                    } else {
                        if (obfExisting == null) {
                            mappings.put(obfuscated, constructNewEntry(obfuscated, original));
                            obfExisting = mappings.get(obfuscated);
                        } else {
                            obfExisting.addOriginal(original);
                            obfExisting.addObfuscated(obfuscated);
                        }
                        if (dupes.get(original) == null) {
                            dupes.put(original, obfExisting);
                        } else {
                            EntryMap dupe = dupes.get(original);
                        }
                    }
                    return;
                }
            } else {
                //throw new RuntimeException("Duplicate obf&name detected! Mapping " + original + " points to " + obfuscated + " " +
                 //       "but this mapping already exists: " + original + " -> " + existing.getObfuscated());
            }
        }

        if (obfExisting != null) {
            // Add as a new original.
            obfExisting.addOriginal(original);
            mappings.put(original, obfExisting);
            return;
        }
        // New mapping.
        mappings.put(obfuscated, constructNewEntry(obfuscated, original));
        mappings.put(original, mappings.get(obfuscated));
    }

    protected String[] getMappings(String original, Map<String, EntryMap> mappings, Map<String, EntryMap> dupes) {
        EntryMap entry = mappings.get(original);
        if (entry == null) {
            return null;
        }
        if (dupes != null) {
            EntryMap existingDupe = dupes.get(original);
            List<String> combined = new ArrayList<>();
            combined.addAll(Arrays.asList(entry.getObfuscated()));
            if (existingDupe != null)  {
                combined.addAll(Arrays.asList(existingDupe.getObfuscated()));
            }
            combined.addAll(Arrays.asList(entry.getOriginals()));
            if (existingDupe != null)  {
                combined.addAll(Arrays.asList(existingDupe.getOriginals()));
            }
            return combined.toArray(new String[0]);
        }
        return entry.getMappings();
    }
}
