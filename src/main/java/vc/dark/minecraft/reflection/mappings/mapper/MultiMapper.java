package vc.dark.minecraft.reflection.mappings.mapper;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.classmap.MultiClassMap;
import vc.dark.minecraft.reflection.mappings.config.Entry;
import vc.dark.minecraft.reflection.mappings.parser.DataWriter;
import vc.dark.minecraft.reflection.mappings.runtime.RuntimeReflectUtils;
import vc.dark.reflection.ReflectClass;

import java.util.*;

public class MultiMapper implements Mapper, DataWriter {

    private Map<String, Mapper> mappers;
    private LinkedHashMultimap<String, ClassMap> obfMappings = LinkedHashMultimap.create();

    public MultiMapper(Map<String, Mapper> mappers) {
        this.mappers = mappers;
    }

    public MultiMapper(Entry[] entries) {
        mappers = new HashMap<>();
        for (Entry e : entries) {
            mappers.put(e.getName(), e.getMapper());
        }
    }

    @Override
    public ReflectClass getClass(String className) throws ClassNotFoundException {
        return RuntimeReflectUtils.getReflectClass(getClassMaps(className));
    }

    @Override
    public ReflectClass getExactClass(String className) throws ClassNotFoundException {
        return RuntimeReflectUtils.getReflectClass(getExactClassMaps(className));
    }

    /**
     * This generates messy (and wrong) permutations of mapping aliases for this class.
     * IE: EntityPanda$AvoidGoal -> EntityPanda$a
     */
    private List<String> nestedAliasMatch(String original, String obfClassName) {
        Set<String> results = new HashSet<>();
        String[] obfSplit = obfClassName.split("\\$");
        String[] origSplit = original.split("\\$");
        if (obfSplit.length != origSplit.length) {
            throw new IllegalArgumentException(obfClassName + " does not have the same number of $ as " + original);
        }
        if (obfSplit.length > 1) {
            for (int i = 0; i < origSplit.length; i++) {
                String baseOrig = origSplit[0];
                String baseObf = obfSplit[0];
                String innerOrig = "";
                String innerObf = "";
                int cursor = 0;
                for (int j = 1; j <= i; j++) {
                    baseOrig += "$" + origSplit[j];
                    baseObf += "$" + obfSplit[j];
                    cursor = j;
                }
                //innerOrig = "$" + origSplit[origSplit.length - 1];
                innerObf = "$" + obfSplit[origSplit.length - 1];
                // Set the current original class to the obf value if at top level.
                // It makes it messy, but it's the only way to get 100% pass with reflectiontester
                // on paper reobf jar
                results.add(baseOrig + innerObf);

                for (ClassMap e : obfMappings.get(baseObf)) {
                    if (!e.getOriginal().equals(baseOrig) && e.getObfuscated().contains(baseObf)) {
                        results.add(e.getOriginal() + innerObf);
                    }
                }
                innerObf = "";
                for (int j = Math.max(1, cursor); j < origSplit.length; j++) {
                    innerObf += "$" + obfSplit[j];
                    for (ClassMap e : obfMappings.get(baseObf)) {
                        int total = e.getOriginal().split("\\$").length + innerObf.split("\\$").length;
                        if (e.getOriginal().contains(innerObf)
                                || total > obfSplit.length) {
                            continue;
                        }
                        results.add(e.getOriginal() + innerObf);
                    }
                }
            }
        }
        return Arrays.asList(results.toArray(new String[0]));
    }

    private ClassMap[] resolveObfMappings(String original, String obfClassName) {
        //return obfMappings.get(obfClassName).toArray(new ClassMap[0]);
        // Make sure this returns a MultiClassMap and a MultiClassMap ONLY.
        Set<ClassMap> maps = new HashSet<>(obfMappings.get(obfClassName));

        // Pre-check
        String obfClass = "";
        for (ClassMap map : maps) {
            if (obfClass.equals("")) {
                Preconditions.checkArgument(!(map.getObfuscated().size() > 1));
                obfClass = map.getObfuscated().get(0);
            } else {
                Preconditions.checkArgument(!(map.getObfuscated().size() > 1));
                //Preconditions.checkArgument(obfClass.equals(map.getObfuscated().get(0)),
                //        "This is not supposed to happen.  Please report a bug at github.com/darkvc/MinecraftReflection/issues.");
            }
        }

        if (obfClass.equals("")) {
            return new ClassMap[0];
        }
        MultiClassMap multiClassMap = new MultiClassMap(original, maps.toArray(new ClassMap[0]));
        // Inner class maps need to be handled differently.
        if (obfClassName.contains("$") && original.contains("$")) {
            for (String m : nestedAliasMatch(original, obfClassName).toArray(new String[0])) {
                multiClassMap.addObfuscated(m);
            }
        }
        return new ClassMap[]{
                multiClassMap
        };
    }

    private ClassMap[] getClassMapsFilterMapper(String className, boolean exact, String skipMapperName) {
        for (Map.Entry<String, Mapper> entry : mappers.entrySet()) {
            if (skipMapperName != null) {
                if (entry.getKey().equals(skipMapperName)) {
                    continue;
                }
            }
            Mapper m = entry.getValue();
            ClassMap[] firstMatch;
            if (!exact) {
                firstMatch = m.getClassMaps(className);
            } else {
                firstMatch = m.getExactClassMaps(className);
            }
            if (firstMatch.length > 0) {
                // Classes should NOT have more than one obfuscated value.
                Preconditions.checkArgument(!(firstMatch.length > 1));
                Preconditions.checkArgument(!(firstMatch[0].getObfuscated().size() > 1));
                return resolveObfMappings(firstMatch[0].getOriginal(), firstMatch[0].getObfuscated().get(0));
            }
        }
        return new ClassMap[0];
    }

    @Override
    public ClassMap[] getClassMaps(String className) {
        return getClassMapsFilterMapper(className, false, null);
    }

    @Override
    public ClassMap[] getExactClassMaps(String className) {
        return getClassMapsFilterMapper(className, true, null);
    }

    @Override
    public Mapper getMapper(String mapping) {
        // TODO: Build the specific mapper
        Mapper origMapper = mappers.get(mapping);
        return new Mapper() {
            @Override
            public ReflectClass getClass(String className) throws ClassNotFoundException {
                return RuntimeReflectUtils.getReflectClass(this.getClassMaps(className));
            }

            @Override
            public ReflectClass getExactClass(String className) throws ClassNotFoundException {
                return RuntimeReflectUtils.getReflectClass(this.getExactClassMaps(className));
            }

            @Override
            public ClassMap[] getClassMaps(String className) {
                ClassMap[] lookup = origMapper.getClassMaps(className);
                if (lookup.length > 0) {
                    Preconditions.checkArgument(!(lookup.length > 1));
                    Preconditions.checkArgument(!(lookup[0].getObfuscated().size() > 1));
                    return resolveObfMappings(lookup[0].getOriginal(), lookup[0].getObfuscated().get(0));
                }
                return getClassMapsFilterMapper(className, false, mapping);
            }

            @Override
            public ClassMap[] getExactClassMaps(String className) {
                ClassMap[] lookup = origMapper.getExactClassMaps(className);
                if (lookup.length > 0) {
                    Preconditions.checkArgument(!(lookup.length > 1));
                    Preconditions.checkArgument(!(lookup[0].getObfuscated().size() > 1));
                    return resolveObfMappings(lookup[0].getOriginal(), lookup[0].getObfuscated().get(0));
                }
                return getClassMapsFilterMapper(className, true, mapping);
            }

            @Override
            public Mapper getMapper(String mapping) {
                return this;
            }
        };
    }

    @Override
    public void clazz(String originalClass, String obfuscatedClass) {
        for (Mapper m : mappers.values()) {
            // Find mapping
            boolean useExactObfMap = (m instanceof RuntimeMapper);
            if (!useExactObfMap) {
                for (ClassMap i : m.getExactClassMaps(originalClass)) {
                    List<String> obfmap = i.getObfuscated();
                    Preconditions.checkArgument(!(obfmap.size() > 1));
                    if (obfmap.get(0).equals(obfuscatedClass)) {
                        obfMappings.put(obfuscatedClass, i);
                    }
                }
            } else {
                RuntimeMapper rm = (RuntimeMapper) m;
                ClassMap r = rm.getExactObfClassMap(originalClass, obfuscatedClass);
                if (r != null) {
                    obfMappings.put(obfuscatedClass, r);
                }
            }
        }
    }

    @Override
    public void field(String originalClass, String obfuscatedClass, String fieldOriginal, String fieldObfuscated) {
        // No-op.
    }

    @Override
    public void method(String originalClass, String obfuscatedClass, String methodOriginal, String methodObfuscated) {
        // No-op.
    }
}
