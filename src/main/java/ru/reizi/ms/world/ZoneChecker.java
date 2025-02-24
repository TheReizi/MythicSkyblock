package ru.reizi.ms.world;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.reizi.ms.Main;

public class ZoneChecker {
    private static final int ZONE_RADIUS = 64;
    private final Main plugin;
    private final IslandManager islandManager;

    public ZoneChecker(Main plugin, IslandManager islandManager) {
        this.plugin = plugin;
        this.islandManager = islandManager;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    Location islandLoc = islandManager.getPlayerIslands().get(player.getUniqueId().toString());
                    if (islandLoc == null) continue;

                    Location playerLoc = player.getLocation();
                    double distance = playerLoc.distance(islandLoc);

                    if (distance > ZONE_RADIUS) {
                        player.teleport(islandLoc.clone().add(0.5, 1, 0.5));
                        player.sendMessage("Ты не можешь покинуть свою зону!");
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}