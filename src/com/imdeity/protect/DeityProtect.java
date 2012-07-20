package com.imdeity.protect;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityPlugin;
import com.imdeity.protect.events.ProtectionListener;

public class DeityProtect extends DeityPlugin {
    
    public static DeityProtect plugin;
    private ProtectionListener protectionListener = new ProtectionListener();
    
    @Override
    protected void initCmds() {
        // No commands
    }
    
    @Override
    protected void initConfig() {
        this.config.addDefaultConfigValue(DeityProtectionConfigHelper.MYSQL_SERVER_ADDRESS, "localhost");
        this.config.addDefaultConfigValue(DeityProtectionConfigHelper.MYSQL_SERVER_PORT, 3306);
        this.config.addDefaultConfigValue(DeityProtectionConfigHelper.MYSQL_DATABASE_NAME, "kingdoms");
        this.config.addDefaultConfigValue(DeityProtectionConfigHelper.MYSQL_DATABASE_USERNAME, "root");
        this.config.addDefaultConfigValue(DeityProtectionConfigHelper.MYSQL_DATABASE_PASSWORD, "root");
    }
    
    @Override
    protected void initDatabase() {
        DeityAPI.getAPI()
                .getDataAPI()
                .getMySQL()
                .write("CREATE TABLE IF NOT EXISTS " + DeityAPI.getAPI().getDataAPI().getMySQL().tableName("deity_protect_", "chunks") + "( " + "`id` INT(16) NOT NULL AUTO_INCREMENT, " + " `world` VARCHAR(30) NOT NULL, " + " `x_coord` INT(16) NOT NULL, " + " `z_coord` INT(16) NOT NULL, "
                        + " `owner` VARCHAR(30), " + " PRIMARY KEY (`id`) " + ") ENGINE = MYISAM;");
    }
    
    @Override
    protected void initInternalDatamembers() {
        ProtectionManager.reload();
    }
    
    @Override
    protected void initLanguage() {
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_BLOCK_BREAK, "&fYou aren't allowed to &cdestroy blocks &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_BLOCK_PLACE, "&fYou aren't allowed to &cplace blocks &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_SIGN_EDIT, "&fYou aren't allowed to &cedit signs &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_ENTER, "&fYou aren't allowed to &center &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_BUCKET_EMPTY, "&fYou aren't allowed to &cempty buckets &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_BUCKET_FILL, "&fYou aren't allowed to &cempty buckets &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_CHEST, "&fYou aren't allowed to &copen chests &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_DOOR, "&fYou aren't allowed to &copen doors &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_BUTTON, "&fYou aren't allowed to &cpush buttons &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_LEVER, "&fYou aren't allowed to &cswitch levers &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_FURNACE, "&fYou aren't allowed to &cuse furnaces &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_BED, "&fYou aren't allowed to &csleep &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_PRESSURE_PLATE, "&fYou aren't allowed to &cuse pressure plates &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_WORKBENCH, "&fYou aren't allowed to &cuse workbenches &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_TRAP_DOOR, "&fYou aren't allowed to &cuse trap doors &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_DISPENSER, "&fYou aren't allowed to &cuse dispensers &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_NOTE_BLOCK, "&fYou aren't allowed to &cuse note blocks &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_JUKEBOX, "&fYou aren't allowed to &cuse jukeboxes &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_BOAT, "&fYou aren't allowed to &cget in boats &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_USE_MINECART, "&fYou aren't allowed to &cget in minecarts &fhere!");
        this.language.addDefaultLanguageValue(DeityProtectLangHelper.INVALID_FIGHT, "&fYou aren't allowed to &cfight &fhere!");
    }
    
    @Override
    protected void initListeners() {
        this.registerListener(protectionListener);
    }
    
    @Override
    protected void initPlugin() {
        plugin = this;
    }
    
    @Override
    protected void initTasks() {
        // No tasks
    }
}
