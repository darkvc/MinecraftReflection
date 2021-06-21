package vc.dark.minecraft.reflection.mappings.parser;

public enum Parsers {

    CSRG(new CSrgParser()),
    YARN(new YarnParser());

    private DataParser parser;

    Parsers(DataParser parser) {
        this.parser = parser;
    }

    public DataParser getParser() {
        return parser;
    }
}
