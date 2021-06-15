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
                    // Brace for impact.
                    ClassMap[] existing = obfHelper.getExactClassMaps(originalClass);
                    if (existing.length < 1) {
                        throw new IllegalArgumentException("Could not find original class: " + originalClass);
                    }
                    obfuscatedClass = existing[0].getObfuscated().get(0);
                }
                parser.field(originalClass, obfuscatedClass, fieldOriginal, fieldObfuscated);
            }

            @Override
            public void method(String originalClass, String obfuscatedClass, String methodOriginal, String methodObfuscated) {
                if (obfuscatedClass.equals("")) {
                    // Brace for impact.
                    ClassMap[] existing = obfHelper.getExactClassMaps(originalClass);
                    if (existing.length < 1) {
                        throw new IllegalArgumentException("Could not find original class: " + originalClass);
                    }
                    obfuscatedClass = existing[0].getObfuscated().get(0);
                }
                parser.method(originalClass, obfuscatedClass, methodOriginal, methodObfuscated);
            }
        };
    }
}
