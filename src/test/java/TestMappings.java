import vc.dark.minecraft.reflection.mappings.Mappings;

public class TestMappings {

    public void testMappings() {
        Mappings.loadMappingsVersion("1.17");
        assert Mappings.hasMappings();
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
        assert Mappings.getClassName("NBTCompressedStreamTools").getMethod("readCompressed").equals("a");
        assert Mappings.getClassName("NBTCompressedStreamTools").getMethod("writeCompressed").equals("a");
        assert Mappings.getClassName("net.minecraft.nbt.NbtIo").getMethod("readCompressed").equals("a");
        assert Mappings.getClassName("NbtIo").getMethod("writeCompressed").equals("a");
        assert Mappings.getClassName("Blocks").getField("CYAN_SHULKER_BOX").equals("jn");
        assert Mappings.getClassName("DifficultyInstance").getField("effectiveDifficulty").equals("e");
        assert Mappings.getClassName("DifficultyDamageScaler").getField("effectiveDifficulty").equals("e");
        assert Mappings.getClassName("MinecraftServer").getOriginal().equals("net.minecraft.server.MinecraftServer");
        assert Mappings.getExactClassName("net.minecraft.server.MinecraftServer").getOriginal().equals("net.minecraft.server.MinecraftServer");
    }
}
