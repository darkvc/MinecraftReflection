package vc.dark.minecraft.reflection.mappings.classmap;

import com.google.common.collect.LinkedHashMultimap;

import java.util.*;

public class ClassMap extends NestedEntryMap {

    private LinkedHashMultimap<String, EntryMap> methods = LinkedHashMultimap.create();
    private LinkedHashMultimap<String, EntryMap> fields = LinkedHashMultimap.create();

    public ClassMap(String obfuscated, String original) {
        super(obfuscated, original);
    }

    public void addMethod(String original, String obfuscated) {
        addMapping(original, obfuscated, methods, true);
    }

    public void addField(String original, String obfuscated) {
        addMapping(original, obfuscated, fields, true);
    }

    public Map<String, Collection<EntryMap>> getMethods() { return Collections.unmodifiableMap(methods.asMap()); }

    public Map<String, Collection<EntryMap>> getFields() { return Collections.unmodifiableMap(fields.asMap()); }

    public String[] getMethods(String original) { return getMappings(original, methods); }

    public String[] getFields(String original) {
        return getMappings(original, fields);
    }
}
