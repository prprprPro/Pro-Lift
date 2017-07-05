package cn.szzxol.pro.lift;

import java.io.File;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author I_promise
 */
public class Lift extends JavaPlugin {

    public static FileConfiguration DefaultConfig;
    public static String version;
    public static File folder;
    public static int StandardDistance;
    public static int StandardHeight;
    public static int LagProtect;
    public static int Speed;
    public static Material baseMaterial;
    public static Material floorMaterial;

    @Override
    public void onEnable() {
        getLogger().info("插件正在加载...");
        this.saveDefaultConfig();
        folder = this.getDataFolder();
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        load();
        version = DefaultConfig.getString("Version");
        getLogger().info("插件加载完成...");
    }

    @Override
    public void onDisable() {
        getLogger().info("插件卸载完成...");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lift") && sender.isOp()) {
            load();
        }
        return true;
    }

    public void load() {
        DefaultConfig = this.getConfig();
        StandardDistance = DefaultConfig.getInt("Lift.MaxDistance");
        StandardHeight = DefaultConfig.getInt("Lift.MaxHeight");
        LagProtect = DefaultConfig.getInt("Setting.LagProtect");
        Speed = DefaultConfig.getInt("Setting.Speed");
        baseMaterial = Material.getMaterial(DefaultConfig.getInt("Block.Base"));
        floorMaterial = Material.getMaterial(DefaultConfig.getInt("Block.Floor"));
    }

}
