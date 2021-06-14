
import joptsimple.internal.Strings;
import vc.dark.minecraft.reflection.mappings.Mappings;

import java.util.Arrays;

public class TestMappings {

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
        }*/
        System.out.println(Strings.join(Mappings.getExactClassMap("net.minecraft.world.scores.Scoreboard").getMethods("removePlayerFromTeam"), ","));
        System.out.println(Strings.join(Mappings.getClassMap("NBTCompressedStreamTools").getMethods("write"), ","));
        System.out.println(Strings.join(Mappings.getClassMap("NbtIo").getMethods("write"), ","));
        System.out.println(Strings.join(Mappings.getClassMap("BlockFire").getFields("flameOdds"), ","));
        System.out.println(Strings.join(Mappings.getClassMap("NBTCompressedStreamTools").getMethods("readCompressed"), ","));
        System.out.println(Strings.join(Mappings.getClassMap("ServerLoginPacketListenerImpl").getMethods("tick"), ","));
        System.out.println(Strings.join(Mappings.getClassMap("LoginListener").getMethods("tick"), ","));
        System.out.println(Strings.join(Mappings.getClassMap("MinecraftServer").getMethods("setMotd"), ","));
        assert Arrays.asList(Mappings.getClassMap("NBTCompressedStreamTools").getMethods("readCompressed")).contains("a");
        assert Arrays.asList(Mappings.getClassMap("NBTCompressedStreamTools").getMethods("writeCompressed")).contains("a");
        assert Arrays.asList(Mappings.getClassMap("net.minecraft.nbt.NbtIo").getMethods("readCompressed")).contains("a");
        assert Arrays.asList(Mappings.getClassMap("NbtIo").getMethods("writeCompressed")).contains("a");
        assert Arrays.asList(Mappings.getClassMap("Blocks").getFields("CYAN_SHULKER_BOX")).contains("jn");
        assert Arrays.asList(Mappings.getClassMap("DifficultyInstance").getFields("effectiveDifficulty")).contains("e");
        assert Arrays.asList(Mappings.getClassMap("DifficultyDamageScaler").getFields("effectiveDifficulty")).contains("e");
        assert Arrays.asList(Mappings.getClassMap("MinecraftServer").getOriginals()).contains("net.minecraft.server.MinecraftServer");
        assert Arrays.asList(Mappings.getExactClassMap("net.minecraft.server.MinecraftServer").getOriginals()).contains("net.minecraft.server.MinecraftServer");
    }
}
