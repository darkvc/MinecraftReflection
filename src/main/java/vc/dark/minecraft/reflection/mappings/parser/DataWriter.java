package vc.dark.minecraft.reflection.mappings.parser;

public interface DataWriter {

    void clazz(String originalClass, String obfuscatedClass);

    void field(String originalClass, String obfuscatedClass, String fieldOriginal, String fieldObfuscated);

    void method(String originalClass, String obfuscatedClass, String methodOriginal, String methodObfuscated);
}
