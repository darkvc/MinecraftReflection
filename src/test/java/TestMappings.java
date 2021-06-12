import vc.dark.minecraft.reflection.mappings.Mappings;

public class TestMappings {

    public void testMappings() {
        Mappings.loadMappingsVersion("1.17");
        assert Mappings.hasMappings;
        /*for (Map.Entry<String, ClassMap> entry : Mappings.classes.entrySet()) {
            ClassMap map = entry.getValue();
            if (entry.getKey().equals("net.minecraft.nbt.DynamicOpsNBT")) {
                //System.out.println(map.original + " -> " + map.obfuscated);
                for (Map.Entry<String, String> f : map.fields.entrySet()) {
                    //System.out.println(map.original + "." + f.getKey() + " -> " + f.getValue());
                }
                for (Map.Entry<String, String> m : map.methods.entrySet()) {
                    //System.out.println(map.original + "." + m.getKey() + " -> " + m.getValue() + "()");
                }
            }
        }*/
        System.out.println(Mappings.getClassName("NBTCompressedStreamTools").getMethod("readCompressed"));
        System.out.println(Mappings.getClassName("NBTCompressedStreamTools").getMethod("writeCompressed"));
        assert Mappings.getClassName("NBTCompressedStreamTools").getMethod("readCompressed") != null;
        assert Mappings.getClassName("NBTCompressedStreamTools").getMethod("readCompressed") != null;
    }
}