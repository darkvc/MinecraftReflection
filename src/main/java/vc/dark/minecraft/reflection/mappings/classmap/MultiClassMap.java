package vc.dark.minecraft.reflection.mappings.classmap;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;

import java.util.*;

public class MultiClassMap extends ClassMap {

    private ClassMap[] classMaps;
    private List<String> injectedAliases = new ArrayList<>();

    public MultiClassMap(String original, ClassMap ...maps) {
        super("", original);
        Preconditions.checkArgument(!(maps.length < 1));
        this.classMaps = maps;
    }

    /* Allows injecting arbitrary class names to fix issues with Inner classes. */
    @Override
    public void addObfuscated(String value) {
        injectedAliases.add(value);
    }

    /* Unsupported operations */

    @Override
    public void addMethod(String original, String obfuscated) {
        throw new UnsupportedOperationException("Cannot call addMethod on a MultiClassMap.");
    }

    @Override
    public void addField(String original, String obfuscated) {
        throw new UnsupportedOperationException("Cannot call addField on a MultiClassMap.");
    }

    @Override
    public List<String> getObfuscated() {
        HashSet<String> uniqueObf = new HashSet<>();
        for (ClassMap e : classMaps) {
            uniqueObf.addAll(e.getObfuscated());
        }
        return Arrays.asList(uniqueObf.toArray(new String[0]));
    }

    private Map<String, Collection<EntryMap>> getCollection(boolean fields) {
        Map<String, Collection<EntryMap>> entries = new HashMap<>();
        for (ClassMap e : classMaps) {
            if (fields) {
                entries.putAll(e.getFields());
            } else {
                entries.putAll(e.getMethods());
            }
        }
        return entries;
    }

    @Override
    public boolean hasObfuscated(String v) {
        return getObfuscated().contains(v);
    }

    @Override
    public Map<String, Collection<EntryMap>> getMethods() {
        return getCollection(false);
    }

    @Override
    public Map<String, Collection<EntryMap>> getFields() {
        return getCollection(true);
    }

    @Override
    public String[] getMethods(String original) {
        // Search.
        HashSet<String> uniqueObf = new HashSet<>();
        for (ClassMap e : classMaps) {
            String[] mappings = e.getMethods(original);
            if (mappings.length > 0) {
                uniqueObf.addAll(Arrays.asList(mappings));
            }
        }
        return uniqueObf.toArray(new String[0]);
    }

    @Override
    public String[] getFields(String original) {
        // Search.
        HashSet<String> uniqueObf = new HashSet<>();
        for (ClassMap e : classMaps) {
            String[] mappings = e.getFields(original);
            if (mappings.length > 0) {
                uniqueObf.addAll(Arrays.asList(mappings));
            }
        }
        return uniqueObf.toArray(new String[0]);
    }

    @Override
    public String[] getMappings() {
        List<String> result = new ArrayList<>();
        for (ClassMap e : classMaps) {
            result.addAll(Arrays.asList(e.getMappings()));
        }
        result.addAll(injectedAliases);
        return result.toArray(new String[0]);
    }
}
