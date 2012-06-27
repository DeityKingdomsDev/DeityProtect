package com.imdeity.protect;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.imdeity.deityapi.Deity;
import com.imdeity.protect.events.ProtectionListener;
import com.imdeity.protect.utils.ChatTools;
import com.imdeity.protect.utils.Database;
import com.imdeity.protect.utils.DeityProtectionConfigHelper;

public class DeityProtect extends JavaPlugin {

    public DeityProtectChat chat = null;
    public static DeityProtect plugin;
    public DeityProtectConfig config = null;
    public Database db = null;

    public void onEnable() {
        plugin = this;
        chat = new DeityProtectChat("DeityProtect");
        config = new DeityProtectConfig(this.getConfig(), "plugins/" + getDescription().getName() + "/config.yml");

        this.config.addDefaultConfigValue(DeityProtectionConfigHelper.MYSQL_SERVER_ADDRESS, "localhost");
        this.config.addDefaultConfigValue(DeityProtectionConfigHelper.MYSQL_SERVER_PORT, 3306);
        this.config.addDefaultConfigValue(DeityProtectionConfigHelper.MYSQL_DATABASE_NAME, "kingdoms");
        this.config.addDefaultConfigValue(DeityProtectionConfigHelper.MYSQL_DATABASE_USERNAME, "root");
        this.config.addDefaultConfigValue(DeityProtectionConfigHelper.MYSQL_DATABASE_PASSWORD, "root");

        db = new Database(false);
        this.getServer().getPluginManager().registerEvents(new ProtectionListener(), this);

        db.write("CREATE TABLE IF NOT EXISTS " + Deity.data.getDB().tableName("deity_protect_", "chunks") + "( " + "`id` INT(16) NOT NULL AUTO_INCREMENT, " + " `world` VARCHAR(30) NOT NULL, " + " `x_coord` INT(16) NOT NULL, " + " `z_coord` INT(16) NOT NULL, " + " `owner` VARCHAR(30), "
                + " PRIMARY KEY (`id`) " + ") ENGINE = MYISAM;");

        chat.out("Enabled");
    }

    public void onDisable() {
        chat.out("Disabled");
    }

    public class DeityProtectChat {
        private String pluginName = "";
        private Logger log = Logger.getLogger("Minecraft");
        private ChatTools chatUtils = new ChatTools();

        public DeityProtectChat(String pluginName) {
            this.pluginName = pluginName;
        }

        public void out(String msg) {
            log.info("[" + pluginName + "] " + msg);
        }

        public void outWarn(String msg) {
            log.warning("[" + pluginName + "] " + msg);
        }

        public void sendPlayerMessage(Player player, String msg) {
            if (player != null) {
                if (player.isOnline()) {
                    this.chatUtils.formatAndSend("<option>&f" + msg, this.pluginName, player);
                }
            }
        }

        public void sendPlayerMessageNoHeader(Player player, String msg) {
            this.chatUtils.formatAndSendWithNewLines(msg, player);
        }

        public void sendGlobalMessage(String msg) {
            for (Player player : Deity.server.getServer().getOnlinePlayers()) {
                this.sendPlayerMessage(player, msg);
            }
        }

        public void sendGlobalNoHeader(String msg) {
            for (Player player : Deity.server.getServer().getOnlinePlayers()) {
                this.sendPlayerMessageNoHeader(player, msg);
            }
        }
    }

    public class DeityProtectConfig {
        private FileConfiguration config = null;
        private File saveFile = null;

        public DeityProtectConfig(FileConfiguration config, String saveLocation) {
            chat.out("Loading Config...");
            this.config = config;
            saveFile = new File(saveLocation);
        }

        public void saveConfig() {
            chat.out("Saving Config...");
            try {
                config.save(saveFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void addDefaultConfigValue(String path, Object value) {
            if (!config.contains(path)) {
                if (value instanceof String) {
                    config.set(path, (String) value);
                } else {
                    config.set(path, value);
                }
            }
        }

        public boolean getBoolean(String path) {
            if (config.contains(path)) { return config.getBoolean(path); }
            return false;
        }

        public List<Boolean> getBooleanList(String path) {
            if (!config.contains(path)) { return config.getBooleanList(path); }
            return null;
        }

        public double getDouble(String path) {
            if (config.contains(path)) { return config.getDouble(path); }
            return 0;
        }

        public int getInt(String path) {
            if (config.contains(path)) { return config.getInt(path); }
            return 0;
        }

        public ItemStack getItemStack(String path) {
            if (config.contains(path)) { return config.getItemStack(path); }
            return null;
        }

        public long getLong(String path) {
            if (config.contains(path)) { return config.getLong(path); }
            return 0;
        }

        public String getString(String path) {
            if (config.contains(path)) { return config.getString(path); }
            return null;
        }
    }
}
