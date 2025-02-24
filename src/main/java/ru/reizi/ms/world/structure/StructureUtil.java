package ru.reizi.ms.world.structure;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import ru.reizi.ms.Main;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

public class StructureUtil {
    public static boolean pasteStructure(Location location, String fileName, Main plugin) {
        Logger logger = plugin.getLogger();
        try {
            File file = new File(plugin.getDataFolder(), "structures/" + fileName);
            if (!file.exists()) {
                logger.warning("Файл структуры " + fileName + " не найден!");
                return false;
            }

            ClipboardFormat format = ClipboardFormats.findByFile(file);
            if (format == null) {
                logger.warning("Формат файла " + fileName + " не поддерживается WorldEdit!");
                return false;
            }

            Clipboard clipboard = format.getReader(new FileInputStream(file)).read();
            ClipboardHolder holder = new ClipboardHolder(clipboard);

            World adaptedWorld = BukkitAdapter.adapt(location.getWorld());
            BlockVector3 position = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            Operations.complete(holder.createPaste(adaptedWorld)
                    .to(position)
                    .ignoreAirBlocks(true)
                    .build());

            logger.info("Структура " + fileName + " успешно загружена на " +
                    location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
            return true;
        } catch (Exception e) {
            logger.severe("Ошибка при загрузке структуры " + fileName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}