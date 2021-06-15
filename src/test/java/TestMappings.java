
import joptsimple.internal.Strings;
import vc.dark.minecraft.reflection.mappings.Mappings;

import java.util.Arrays;

public class TestMappings {

    /*
    public void testMappings() {
        Mappings.loadMappingsVersion("1.17");
        assert Mappings.hasMappings();
        /*for (Map.Entry<String, OldClassMap> entry : Mappings.classes.entrySet()) {
            OldClassMap map = entry.getValue();
            if (entry.getKey()).contains("net.minecraft.nbt.DynamicOpsNBT")) {
                //System.out.println(map.original + " -> " + map.obfuscated);
                for (Map.Entry<String, String> f : map.fields.entrySet()) {
                    //System.out.println(map.original + "." + f.getKey() + " -> " + f.getValue());
                }
                for (Map.Entry<String, String> m : map.methods.entrySet()) {
                    //System.out.println(map.original + "." + m.getKey() + " -> " + m.getValue() + "()");
                }
            }
        }
        System.out.println(Strings.join(Mappings.getExactClassMaps("net.minecraft.world.scores.Scoreboard").getMethods("removePlayerFromTeam"), ","));
        System.out.println(Strings.join(Mappings.getClassMaps("NBTCompressedStreamTools").getMethods("write"), ","));
        System.out.println(Strings.join(Mappings.getClassMaps("NbtIo").getMethods("write"), ","));
        System.out.println(Strings.join(Mappings.getClassMaps("BlockFire").getFields("flameOdds"), ","));
        System.out.println(Strings.join(Mappings.getClassMaps("NBTCompressedStreamTools").getMethods("readCompressed"), ","));
        System.out.println(Strings.join(Mappings.getClassMaps("ServerLoginPacketListenerImpl").getMethods("tick"), ","));
        System.out.println(Strings.join(Mappings.getClassMaps("LoginListener").getMethods("tick"), ","));
        System.out.println(Strings.join(Mappings.getClassMaps("MinecraftServer").getMethods("setMotd"), ","));
        assert Arrays.asList(Mappings.getClassMaps("NBTCompressedStreamTools").getMethods("readCompressed")).contains("a");
        assert Arrays.asList(Mappings.getClassMaps("NBTCompressedStreamTools").getMethods("writeCompressed")).contains("a");
        assert Arrays.asList(Mappings.getClassMaps("net.minecraft.nbt.NbtIo").getMethods("readCompressed")).contains("a");
        assert Arrays.asList(Mappings.getClassMaps("NbtIo").getMethods("writeCompressed")).contains("a");
        assert Arrays.asList(Mappings.getClassMaps("Blocks").getFields("CYAN_SHULKER_BOX")).contains("jn");
        assert Arrays.asList(Mappings.getClassMaps("DifficultyInstance").getFields("effectiveDifficulty")).contains("e");
        assert Arrays.asList(Mappings.getClassMaps("DifficultyDamageScaler").getFields("effectiveDifficulty")).contains("e");
        assert Arrays.asList(Mappings.getClassMaps("MinecraftServer").getOriginals()).contains("net.minecraft.server.MinecraftServer");
        assert Arrays.asList(Mappings.getExactClassMaps("net.minecraft.server.MinecraftServer").getOriginals()).contains("net.minecraft.server.MinecraftServer");
    }*/
}
