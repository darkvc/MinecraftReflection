package vc.dark.minecraft.reflection.mappings;

import java.util.Map;

public class ClassAlias extends ClassMap {
    private ClassMap aliased;
    private Map<String, ClassMap> mappings;

    public ClassAlias(String original, String obfuscated, Map<String, ClassMap> classes) throws ClassNotFoundException {
        super(original, obfuscated);
        aliased = classes.get(obfuscated);
        this.mappings = classes;
        if (aliased == null) {
            // late load.
            //throw new ClassNotFoundException("Could not find class mapping " + obfuscated);
        }
    }

    private void resolveAlias() {
        if (aliased == null) {
            aliased = this.mappings.get(super.getObfuscated());
        }
    }

    public boolean isAlias() {
        return true;
    }

    public String getAlias() {
        return super.getObfuscated();
    }

    public ClassMap getAliasMap() {
        resolveAlias();
        return aliased;
    }

    @Override
    public String getObfuscated() {
        resolveAlias();
        return aliased.getObfuscated();
    }

    @Override
    public Map<String, String> getMethods() {
        resolveAlias();
        return aliased.getMethods();
    }

    @Override
    public Map<String, String> getFields() {
        resolveAlias();
        return aliased.getFields();
    }

    @Override
    public void addMethod(String original, String obfuscated) {
        resolveAlias();
        aliased.addMethod(original, obfuscated);
    }

    @Override
    public void addField(String original, String obfuscated) {
        resolveAlias();
        aliased.addField(original, obfuscated);
    }

    @Override
    public String getMethod(String original) {
        resolveAlias();
        return aliased.getMethod(original);
    }

    @Override
    public String getField(String original) {
        resolveAlias();
        return aliased.getField(original);
    }
}
