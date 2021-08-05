package vc.dark.minecraft.reflection.mappings.parser;

public enum Parsers {

    CSRG(new CSrgParser()),
    PROGUARD(new ProguardParser());

    private DataParser parser;

    Parsers(DataParser parser) {
        this.parser = parser;
    }

    public DataParser getParser() {
        return parser;
    }
}
