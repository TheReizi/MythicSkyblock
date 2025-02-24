package ru.reizi.ms.commands.island;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.reizi.ms.Main;
import ru.reizi.ms.commands.admin.AdminCommandManager;
import ru.reizi.ms.commands.admin.StructureCommand;
import ru.reizi.ms.world.IslandManager;

public class IslandCommandManager implements CommandExecutor {
    private final Main main;
    private final IslandManager islandManager;
    private final CreateCommand createCommand;
    private final AdminCommandManager adminCommandManager;

    public IslandCommandManager(Main main, IslandManager islandManager, StructureCommand structureCommand) {
        this.main = main;
        this.islandManager = islandManager;
        this.createCommand = new CreateCommand(main, islandManager);
        this.adminCommandManager = new AdminCommandManager(main, structureCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("Использование /island");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                return createCommand.onCommand(sender, command, label, args);
            case "admin":
                return adminCommandManager.onCommand(sender, command, label, args);

            default:
                player.sendMessage("Неизвестная команда: " + subCommand);
                return true;
        }
    }
}
