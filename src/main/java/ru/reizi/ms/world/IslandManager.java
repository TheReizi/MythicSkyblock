package ru.reizi.ms.world;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.reizi.ms.Main;
import ru.reizi.ms.world.structure.StructureUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class IslandManager {
    private final Main plugin;
    private final Map<String, Location> playerIslands = new HashMap<>();
    private int nextIslandX = 0;
    private final int islandSpacing = 128;
    private final int borderRadius = 64;

    public IslandManager(Main plugin) {
        this.plugin = plugin;
    }

    public void createSkyblockWorld() {
        World skyblockWorld = plugin.getServer().getWorld("skyblock_world");
        if (skyblockWorld == null) {
            plugin.getLogger().warning("Мир скайблока не найден при запуске!");
            return;
        }

        File structuresDir = new File(plugin.getDataFolder(), "structures");
        if (!structuresDir.exists()) {
            structuresDir.mkdirs();
            copyStructuresFromResources(structuresDir);
        }
    }

    private void copyStructuresFromResources(File structuresDir) {
        try {
            File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            if (!jarFile.isFile()) {
                plugin.getLogger().warning("Не удалось найти JAR-файл плагина для копирования структур!");
                return;
            }

            try (JarFile jar = new JarFile(jarFile)) {
                jar.stream()
                        .filter(entry -> entry.getName().startsWith("structures/") && !entry.isDirectory())
                        .forEach(entry -> {
                            String fileName = entry.getName().substring("structures/".length());
                            File targetFile = new File(structuresDir, fileName);
                            if (!targetFile.exists()) {
                                try (InputStream in = jar.getInputStream(entry);
                                     FileOutputStream out = new FileOutputStream(targetFile)) {
                                    byte[] buffer = new byte[1024];
                                    int bytesRead;
                                    while ((bytesRead = in.read(buffer)) != -1) {
                                        out.write(buffer, 0, bytesRead);
                                    }
                                    plugin.getLogger().info("Скопирован файл структуры: " + fileName);
                                } catch (Exception e) {
                                    plugin.getLogger().warning("Ошибка при копировании файла " + fileName + ": " + e.getMessage());
                                }
                            }
                        });
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Не удалось скопировать структуры из resources: " + e.getMessage());
        }
    }

    public void createPlayerIsland(Player player) {
        World world = plugin.getServer().getWorld("skyblock_world");
        if (world == null) {
            player.sendMessage("Мир skyblock_world не найден!");
            return;
        }

        File configFile = new File(plugin.getDataFolder(), "structure.yml");
        if (!configFile.exists()) {
            plugin.saveResource("structure.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String structureFileName = config.getString("structure-file", "skyblock_island.schem");

        double centerX = config.getDouble("center.x", nextIslandX);
        double centerY = config.getDouble("center.y", 72.0);
        double centerZ = config.getDouble("center.z", 0.0);
        float pitch = (float) config.getDouble("center.pitch", 0.0);
        float yaw = (float) config.getDouble("center.yaw", 0.0);

        Location centerLoc = new Location(world, centerX, centerY, centerZ, yaw, pitch);
        nextIslandX += islandSpacing;

        if (!StructureUtil.pasteStructure(centerLoc.subtract(getStructureCenterOffset(structureFileName)), structureFileName, plugin)) {
            player.sendMessage("Не удалось загрузить структуру острова.");
            return;
        }

        player.teleport(centerLoc);
        playerIslands.put(player.getUniqueId().toString(), centerLoc);

        startBorderVisualization(player, centerLoc);

        player.sendMessage("Твой остров создан! Зона ограничена радиусом 64 блока.");
    }

    private Location getStructureCenterOffset(String structureFileName) {
        return new Location(null, 0, 0, 0);
    }

    private void startBorderVisualization(Player player, Location center) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !playerIslands.containsKey(player.getUniqueId().toString())) {
                    cancel();
                    return;
                }

                World world = center.getWorld();
                double y = center.getY() + 1;

                for (int i = 0; i < 360; i += 5) {
                    double angle = Math.toRadians(i);
                    double x = center.getX() + borderRadius * Math.cos(angle);
                    double z = center.getZ() + borderRadius * Math.sin(angle);
                    Location particleLoc = new Location(world, x, y, z);
                    player.spawnParticle(Particle.REDSTONE, particleLoc, 1, new Particle.DustOptions(org.bukkit.Color.RED, 1));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public Map<String, Location> getPlayerIslands() {
        return playerIslands;
    }
}