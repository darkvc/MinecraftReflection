package vc.dark.minecraft.reflection.mappings.runtime;

import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.mapper.Mapper;
import vc.dark.minecraft.reflection.mappings.mapper.RuntimeMapper;
import vc.dark.minecraft.reflection.mappings.parser.DataWriter;

public class ObfuscatedClassHelper {
    private Mapper obfHelper;
    private DataWriter obfWriter;

    public ObfuscatedClassHelper() {
        this.obfHelper = new RuntimeMapper();
        this.obfWriter = (DataWriter) this.obfHelper;
    }

    public ObfuscatedClassHelper(Mapper existing, DataWriter output) {
        this.obfHelper = existing;
        this.obfWriter = output;
    }

    public Mapper getMapper() {
        return obfHelper;
    }

    public DataWriter wrap(DataWriter parser) {
        return new DataWriter() {
            // Passthru/add class.
            @Override
            public void clazz(String originalClass, String obfuscatedClass) {
                obfWriter.clazz(originalClass, obfuscatedClass);
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
