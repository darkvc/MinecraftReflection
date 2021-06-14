package vc.dark.minecraft.reflection.mappings.parser;

public class MultiWriter implements DataWriter {

    private DataWriter[] writers;

    public MultiWriter(DataWriter ...writersOfTheStorm) {
        writers = writersOfTheStorm;
    }

    @Override
    public void clazz(String originalClass, String obfuscatedClass) {
        for (DataWriter writer : writers) {
            writer.clazz(originalClass, obfuscatedClass);
        }
    }

    @Override
    public void field(String originalClass, String obfuscatedClass, String fieldOriginal, String fieldObfuscated) {
        for (DataWriter writer : writers) {
            writer.field(originalClass, obfuscatedClass, fieldOriginal, fieldObfuscated);
        }
    }

    @Override
    public void method(String originalClass, String obfuscatedClass, String methodOriginal, String methodObfuscated) {
        for (DataWriter writer : writers) {
            writer.method(originalClass, obfuscatedClass, methodOriginal, methodObfuscated);
        }
    }
}
