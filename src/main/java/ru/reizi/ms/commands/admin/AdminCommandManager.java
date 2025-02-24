package ru.reizi.ms.commands.admin;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.reizi.ms.Main;

public class AdminCommandManager implements CommandExecutor {
    private Main main;
    private StructureCommand structureCommand;

    public AdminCommandManager(Main main, StructureCommand structureCommand) {
        this.main = main;
        this.structureCommand = structureCommand;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("mythicskyblock.admin")) {
            player.sendMessage("У вас нет прав для выполнения этой команды!");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("Использование: /island admin <команда> [аргументы]");
            return true;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "structure":
                return structureCommand.execute(player, args);

            default:
                player.sendMessage("Неизвестная команда: " + subCommand);
                return true;
        }
    }
}
