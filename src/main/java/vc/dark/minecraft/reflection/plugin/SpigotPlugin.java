package vc.dark.minecraft.reflection.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import vc.dark.minecraft.reflection.MinecraftReflection;

public class SpigotPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        MinecraftReflection.loadMappings(Bukkit.getVersion());
    }
}
