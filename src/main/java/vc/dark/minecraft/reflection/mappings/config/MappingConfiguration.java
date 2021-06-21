package vc.dark.minecraft.reflection.mappings.config;

import com.google.common.base.Preconditions;
import vc.dark.minecraft.reflection.mappings.mapper.Mapper;
import vc.dark.minecraft.reflection.mappings.mapper.MultiMapper;
import vc.dark.minecraft.reflection.mappings.parser.MultiWriter;
import vc.dark.minecraft.reflection.mappings.runtime.Cache;
import vc.dark.minecraft.reflection.mappings.runtime.ObfuscatedClassHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MappingConfiguration {
    private Entry[] entries;


    public MappingConfiguration(Entry ...entries) {
        this.entries = entries;
    }

    public MultiMapper apply(String version) {
        // Make multimapper a thing.
//        Map<String, Mapper> mapEntries = new HashMap<>();
//        for (Entry entry : entries) {
//            Preconditions.checkArgument(entry.getMapper() != null);
//            mapEntries.put(entry.getName(), entry.getMapper());
//        }
        MultiMapper multiMapper = new MultiMapper(entries);
        for (Entry entry : entries) {
            System.out.println("Processing " + entry.getName() + " entries...");
            Cache cache = new Cache(version, entry.getName());
            if (cache.cacheExists()) {
                cache.parse(null, new MultiWriter(entry.getMapper(), multiMapper));
            } else {
                try {
                    cache.openFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ObfuscatedClassHelper helper = new ObfuscatedClassHelper(entry.getMapper(), entry.getMapper());
                entry.getParser().parse(null, helper.wrap(new MultiWriter(cache, entry.getMapper(), multiMapper)));
            }
        }
        return multiMapper;
    }
}
