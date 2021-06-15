import vc.dark.minecraft.reflection.mappings.Mappings;
import vc.dark.minecraft.reflection.mappings.classmap.ClassMap;
import vc.dark.minecraft.reflection.mappings.parser.DataWriter;
import vc.dark.minecraft.reflection.mappings.runtime.Cache;

import java.util.Arrays;
import java.util.List;

public class TestMappingValidator implements DataWriter {
    private int count = 0;
    public void testMappingsv2() {
        Mappings.loadMappingsVersion("1.17");
        assert Mappings.hasMappings();

        Cache tester = new Cache("1.17");
        assert tester.cacheExists();
        tester.parse(null, this);
        // check 62765
    }

    @Override
    public void clazz(String originalClass, String obfuscatedClass) {
        // Validate class exists.
        assert Mappings.getExactClassMaps(originalClass) != null;
        System.out.println("Class " + originalClass + " -> " + Arrays.toString(Mappings.getExactClassMaps(originalClass)));
        count++;
    }

    @Override
    public void field(String originalClass, String obfuscatedClass, String fieldOriginal, String fieldObfuscated) {
        boolean found = false;
        for (ClassMap e : Mappings.getExactClassMaps(originalClass)) {
            List<String> test = Arrays.asList(e.getFields(fieldOriginal));
//            System.out.println("Class: " + originalClass + " (" + obfuscatedClass + ") " + "Field" + " " + fieldOriginal + " (" + fieldObfuscated + ")");
//            System.out.println(Arrays.toString(test.toArray(new String[0])));
            if (test.contains(fieldObfuscated) && test.contains(fieldOriginal)) {
                found = true;
                count++;
            }
        }
        assert found;
    }

    @Override
    public void method(String originalClass, String obfuscatedClass, String methodOriginal, String methodObfuscated) {
        boolean found = false;
        for (ClassMap e : Mappings.getExactClassMaps(originalClass)) {
            List<String> test = Arrays.asList(e.getMethods(methodOriginal));
            //System.out.println("Class: " + originalClass + " (" + obfuscatedClass + ") " + "Method" + " " + methodOriginal + " (" + methodObfuscated + ")");
//            System.out.println(Arrays.toString(test.toArray(new String[0])));
            if (test.contains(methodObfuscated) && test.contains(methodOriginal)) {
                found = true;
                count++;
            }
        }
        assert found;
    }
}
