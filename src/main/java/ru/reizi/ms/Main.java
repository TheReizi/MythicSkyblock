package ru.reizi.ms;

import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import ru.reizi.ms.commands.admin.StructureCommand;
import ru.reizi.ms.commands.island.IslandCommandManager;
import ru.reizi.ms.world.IslandManager;
import ru.reizi.ms.world.ZoneChecker;
import ru.reizi.ms.world.structure.StructureToolListener;
import ru.reizi.ms.world.worldgen.VoidGenerator;

import java.util.Objects;

public final class Main extends JavaPlugin {
    private IslandManager islandManager;
    private ZoneChecker zoneChecker;

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.BLUE + "-----------------------");
        getLogger().info(ChatColor.BLUE + " ");
        getLogger().info(ChatColor.BLUE + "MythicSkyblock by Reizi");
        getLogger().info(ChatColor.BLUE + "Включён!");
        getLogger().info(ChatColor.BLUE + " ");
        getLogger().info(ChatColor.BLUE + "-----------------------");

        islandManager = new IslandManager(this);
        zoneChecker = new ZoneChecker(this, islandManager);

        WorldCreator worldCreator = new WorldCreator("skyblock_world");
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generator(new VoidGenerator());
        worldCreator.createWorld();

        StructureCommand structureCommand = new StructureCommand(this);
        IslandCommandManager commandManager = new IslandCommandManager(this, islandManager, structureCommand);
        Objects.requireNonNull(getCommand("island")).setExecutor(commandManager);

        getServer().getPluginManager().registerEvents(new StructureToolListener(this, structureCommand), this);

        zoneChecker.start();
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "-----------------------");
        getLogger().info(ChatColor.RED + " ");
        getLogger().info(ChatColor.RED + "MythicSkyblock by Reizi");
        getLogger().info(ChatColor.RED + "Выключен!");
        getLogger().info(ChatColor.RED + " ");
        getLogger().info(ChatColor.RED + "-----------------------");
    }

    public IslandManager getIslandManager() {
        return islandManager;
    }
}
