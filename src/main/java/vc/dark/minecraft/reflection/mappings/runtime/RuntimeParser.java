package vc.dark.minecraft.reflection.mappings.runtime;

import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.parser.DataWriter;

public class RuntimeParser {
    private RuntimeMapper obfHelper;

    public RuntimeParser() {
        this.obfHelper = new RuntimeMapper();
    }

    public RuntimeParser(RuntimeMapper existing) {
        this.obfHelper = existing;
    }

    public RuntimeMapper getMapper() {
        return obfHelper;
    }

    public DataWriter wrap(DataWriter parser) {
        return new DataWriter() {
            // Passthru/add class.
            @Override
            public void clazz(String originalClass, String obfuscatedClass) {
                obfHelper.clazz(originalClass, obfuscatedClass);
                parser.clazz(originalClass, obfuscatedClass);
            }

            @Override
            public void field(String originalClass, String obfuscatedClass, String fieldOriginal, String fieldObfuscated) {
                if (obfuscatedClass.equals("")) {
                    ClassMap existing = obfHelper.getExactClassMap(originalClass);
                    if (existing == null) {
                        throw new IllegalArgumentException("Could not find original class: " + originalClass);
                    }
                    // Get first obfuscated value, if missing
                    obfuscatedClass = existing.getObfuscated()[0];
                }
                parser.field(originalClass, obfuscatedClass, fieldOriginal, fieldObfuscated);
            }

            @Override
            public void method(String originalClass, String obfuscatedClass, String methodOriginal, String methodObfuscated) {
                if (obfuscatedClass.equals("")) {
                    ClassMap existing = obfHelper.getExactClassMap(originalClass);
                    if (existing == null) {
                        throw new IllegalArgumentException("Could not find original class: " + originalClass);
                    }
                    // Get first obfuscated value, if missing
                    obfuscatedClass = existing.getObfuscated()[0];
                }
                parser.method(originalClass, obfuscatedClass, methodOriginal, methodObfuscated);
            }
        };
    }
}
