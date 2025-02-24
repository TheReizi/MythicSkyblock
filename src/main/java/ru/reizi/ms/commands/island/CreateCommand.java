package ru.reizi.ms.commands.island;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.reizi.ms.Main;
import ru.reizi.ms.world.IslandManager;

public class CreateCommand implements CommandExecutor {
    private final Main plugin;
    private final IslandManager islandManager;

    public CreateCommand(Main plugin, IslandManager islandManager) {
        this.plugin = plugin;
        this.islandManager = islandManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || !args[0].equalsIgnoreCase("create")) {
            player.sendMessage("Использование: /island create");
            return true;
        }

        Location existingIsland = islandManager.getPlayerIslands().get(player.getUniqueId().toString());
        if (existingIsland != null) {
            player.teleport(existingIsland.clone().add(0.5, 2, 0.5));
            player.sendMessage("Ты телепортирован на свой остров!");
            return true;
        }

        islandManager.createPlayerIsland(player);
        return true;
    }
}