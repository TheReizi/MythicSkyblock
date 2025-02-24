package ru.reizi.ms.commands.admin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.reizi.ms.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class StructureCommand {
    private final Main main;
    private final Map<String, Location> pos1 = new HashMap<>();
    private final Map<String, Location> pos2 = new HashMap<>();

    public StructureCommand(Main main) {
        this.main = main;
    }

    public boolean execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Использование: /island admin structure <pos1|pos2|save|tool> [название]");
            return true;
        }

        String action = args[2].toLowerCase();

        switch (action) {
            case "pos1":
                pos1.put(player.getUniqueId().toString(), player.getLocation());
                player.sendMessage("Первая точка установлена: " + player.getLocation().toVector());
                return true;

            case "pos2":
                pos2.put(player.getUniqueId().toString(), player.getLocation());
                player.sendMessage("Вторая точка установлена: " + player.getLocation().toVector());
                return true;

            case "save":
                if (args.length < 4) {
                    player.sendMessage("Укажите название структуры: /island admin structure save <название>");
                    return true;
                }
                String structureName = args[3];
                return saveStructure(player, structureName);

            case "tool":
                giveStructureTool(player);
                return true;

            default:
                player.sendMessage("Неизвестная команда: " + action);
                return true;
        }
    }

    private void giveStructureTool(Player player) {
        ItemStack tool = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = tool.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Создатель структур");
            tool.setItemMeta(meta);
        }
        player.getInventory().addItem(tool);
        player.sendMessage("Вы получили инструмент 'Создатель структур'. ЛКМ — первая точка, ПКМ — вторая точка.");
    }

    private boolean saveStructure(Player player, String structureName) {
        Location p1 = pos1.get(player.getUniqueId().toString());
        Location p2 = pos2.get(player.getUniqueId().toString());

        if (p1 == null || p2 == null) {
            player.sendMessage("Сначала установите обе точки с помощью pos1 и pos2 или инструмента!");
            return true;
        }

        WorldEditPlugin worldEdit = (WorldEditPlugin) main.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            player.sendMessage("WorldEdit не установлен!");
            return true;
        }

        try {
            Region region = new CuboidRegion(
                    BukkitAdapter.adapt(p1.getWorld()),
                    BlockVector3.at(p1.getX(), p1.getY(), p1.getZ()),
                    BlockVector3.at(p2.getX(), p2.getY(), p2.getZ())
            );

            Clipboard clipboard = new BlockArrayClipboard(region);
            World adaptedWorld = BukkitAdapter.adapt(p1.getWorld());
            Operations.complete(new ForwardExtentCopy(
                    adaptedWorld, region, clipboard, region.getMinimumPoint()
            ));

            File structuresDir = new File(main.getDataFolder(), "structures");
            if (!structuresDir.exists()) {
                structuresDir.mkdirs();
            }

            File schematicFile = new File(structuresDir, structureName + ".schem");
            main.getLogger().info("Сохраняем структуру в: " + schematicFile.getAbsolutePath());

            ClipboardFormat format = ClipboardFormats.findByAlias("sponge");
            if (format == null) {
                main.getLogger().warning("Формат .schem не поддерживается этой версией WorldEdit!");
                player.sendMessage("Ошибка: Формат .schem не поддерживается!");
                return true;
            }

            try (ClipboardWriter writer = format.getWriter(new FileOutputStream(schematicFile))) {
                writer.write(clipboard);
            }

            Location spawnLoc = player.getLocation();

            File configFile = new File(main.getDataFolder(), "structure.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            config.set("structure-file", structureName + ".schem");
            config.set("center.x", spawnLoc.getX());
            config.set("center.y", spawnLoc.getY());
            config.set("center.z", spawnLoc.getZ());
            config.set("center.pitch", spawnLoc.getPitch());
            config.set("center.yaw", spawnLoc.getYaw());
            config.save(configFile);

            player.sendMessage("Структура " + structureName + " сохранена с точкой спавна: " + player.getLocation().toVector());
            return true;
        } catch (Exception e) {
            player.sendMessage("Ошибка при сохранении структуры: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    public Map<String, Location> getPos1() {
        return pos1;
    }

    public Map<String, Location> getPos2() {
        return pos2;
    }
}
