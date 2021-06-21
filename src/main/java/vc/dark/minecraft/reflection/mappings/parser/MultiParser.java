package vc.dark.minecraft.reflection.mappings.parser;

import vc.dark.minecraft.reflection.mappings.parser.DataParser;
import vc.dark.minecraft.reflection.mappings.parser.DataWriter;

public class MultiParser implements DataParser {
    private DataParser[] parsers;

    public MultiParser(DataParser ...parsers) {
        this.parsers = parsers;
    }

    @Override
    public void parse(String[] lines, DataWriter out) {
        for (DataParser parser : parsers) {
            parser.parse(lines, out);
        }
    }
}
