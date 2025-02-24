package ru.reizi.ms.world.structure;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.reizi.ms.Main;
import ru.reizi.ms.commands.admin.StructureCommand;


public class StructureToolListener implements Listener {
    private final Main main;
    private final StructureCommand structureCommand;

    public StructureToolListener(Main main, StructureCommand structureCommand) {
        this.main = main;
        this.structureCommand = structureCommand;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.DIAMOND_AXE || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !meta.getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "Создатель структур")) {
            return;
        }

        Action action = event.getAction();
        Location clickedLocation = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : player.getLocation();

        if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            structureCommand.getPos1().put(player.getUniqueId().toString(), clickedLocation);
            player.sendMessage("Первая точка установлена: " + clickedLocation.toVector());
            event.setCancelled(true);
        } else if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            structureCommand.getPos2().put(player.getUniqueId().toString(), clickedLocation);
            player.sendMessage("Вторая точка установлена: " + clickedLocation.toVector());
            event.setCancelled(true);
        }
    }
}
