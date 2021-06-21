package vc.dark.minecraft.reflection.mappings.config;

import vc.dark.minecraft.reflection.mappings.mapper.Mapper;
import vc.dark.minecraft.reflection.mappings.mapper.MultiMapper;
import vc.dark.minecraft.reflection.mappings.mapper.RuntimeMapper;
import vc.dark.minecraft.reflection.mappings.parser.DataParser;
import vc.dark.minecraft.reflection.mappings.parser.MultiParser;
import vc.dark.minecraft.reflection.mappings.parser.MultiWriter;

public class Entry {
    private DataParser[] parsers;
    private String mapName;
    private RuntimeMapper mapper;

    public Entry(String mapName, DataParser ...parsers) {
        this.mapName = mapName;
        this.parsers = parsers;
        this.mapper = new RuntimeMapper();
    }

    public String getName() {
        return mapName;
    }

    public MultiParser getParser() {
        return new MultiParser(parsers);
    }

    public RuntimeMapper getMapper() {
        return mapper;
    }
}
