package vc.dark.minecraft.reflection.mappings.classmap;

import java.util.*;

public class ClassMap extends NestedEntryMap {

    Map<String, EntryMap> methods = new HashMap<>();
    Map<String, EntryMap> duplicateMethods = new HashMap<>();
    Map<String, EntryMap> fields = new HashMap<>();
    Map<String, EntryMap> duplicateFields = new HashMap<>();

    public ClassMap(String obfuscated, String original) {
        super(obfuscated, original);
    }

    public void addMethod(String original, String obfuscated) {
        addMapping(original, obfuscated, methods, duplicateMethods);
    }

    public void addField(String original, String obfuscated) {
        addMapping(original, obfuscated, fields, duplicateFields);
    }

    public Map<String, EntryMap> getMethods() { return methods; }

    public Map<String, EntryMap> getDuplicateMethods() { return duplicateMethods; }

    /**
     * @deprecated This will likely return duplicate mappings.
     */
    @Deprecated
    public Map<String, EntryMap> getFields() { return fields; }

    public Map<String, EntryMap> getDuplicateFields() { return duplicateFields; }

    public String[] getMethods(String original) { return getMappings(original, methods, duplicateMethods); }

    public String[] getFields(String original) {
        return getMappings(original, fields, duplicateFields);
    }
}
