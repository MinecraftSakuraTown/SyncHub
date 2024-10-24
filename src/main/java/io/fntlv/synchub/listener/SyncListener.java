package io.fntlv.synchub.listener;

import br.com.finalcraft.evernifecore.listeners.base.ECListener;
import br.com.finalcraft.evernifecore.locale.FCLocale;
import br.com.finalcraft.evernifecore.locale.LocaleMessage;
import io.fntlv.synchub.handlers.SyncHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SyncListener implements ECListener {

    public static void register(JavaPlugin plugin){
        ECListener.register(plugin, SyncListener.class);
    }

    @FCLocale(lang = "ZH_CN", text = "&7[&6系统&7] &c数据保存时出现异常,请联系管理员!")
    public static LocaleMessage DENY_NOT_SAVE_IN_SERVER;

    @FCLocale(lang = "ZH_CN", text = "&7[&6系统&7] &c数据保存失败,请联系管理员!")
    public static LocaleMessage DENY_SAVE_FAIL;

    @FCLocale(lang = "ZH_CN", text = "&7[&6系统&7] &c数据正在保存中,请稍后重试!")
    public static LocaleMessage DENY_SAVING;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        SyncHandler syncHandler = SyncHandler.getInstance();
        SyncHandler.LoginResult loginResult = syncHandler.loginAndSyncData(player);
        if (loginResult.equals(SyncHandler.LoginResult.ALLOW)){
            return;
        }
        switch (loginResult){
            case DENY_NOT_SAVE_IN_SERVER:
                event.disallow
                        (PlayerLoginEvent.Result.KICK_OTHER,
                                DENY_NOT_SAVE_IN_SERVER
                                        .getFancyText(player).getText()
                                        .replace("&","§")
                        );
                break;
            case DENY_SAVE_FAIL:
                event.disallow
                        (PlayerLoginEvent.Result.KICK_OTHER,
                                DENY_SAVE_FAIL
                                        .getFancyText(player).getText()
                                        .replace("&","§")
                        );
                break;
            case DENY_SAVING:
                event.disallow
                        (PlayerLoginEvent.Result.KICK_OTHER,
                                DENY_SAVING
                                        .getFancyText(player).getText()
                                        .replace("&","§")
                        );

        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event){
        Player player = event.getPlayer();
        SyncHandler.getInstance().saveData(player);
    }

}
