package io.fntlv.synchub.listener;

import br.com.finalcraft.evernifecore.listeners.base.ECListener;
import io.fntlv.synchub.handlers.SyncHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PreventListener implements ECListener {

    public static void register(JavaPlugin plugin){
        ECListener.register(plugin, PreventListener.class);
    }

//    @FCLocale(lang = "ZH_CN", text = "&7[&6系统&7] &c数据正在加载中,请稍等!")
//    public static LocaleMessage NOT_LOADED;

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if (!SyncHandler.getInstance().isPlayerSync(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickUp(PlayerPickupItemEvent event){
        Player player = event.getPlayer();
        if (!SyncHandler.getInstance().isPlayerSync(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDrag(InventoryDragEvent event){
        HumanEntity whoClicked = event.getWhoClicked();
        if (!(whoClicked instanceof Player)){
            return;
        }
        Player player = (Player) whoClicked;
        if (!SyncHandler.getInstance().isPlayerSync(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event){
        HumanEntity whoClicked = event.getWhoClicked();
        if (!(whoClicked instanceof Player)){
            return;
        }
        Player player = (Player) whoClicked;
        if (!SyncHandler.getInstance().isPlayerSync(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerOpenInventory(InventoryOpenEvent event){
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player)){
            return;
        }
        Player player = (Player) humanEntity;
        if (!SyncHandler.getInstance().isPlayerSync(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)){
            return;
        }
        Player player = (Player) entity;
        if (!SyncHandler.getInstance().isPlayerSync(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if (!SyncHandler.getInstance().isPlayerSync(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!SyncHandler.getInstance().isPlayerSync(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        if (!SyncHandler.getInstance().isPlayerSync(player)){
            event.setCancelled(true);
        }
    }

    // maybe we can add a white list;
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        if (!SyncHandler.getInstance().isPlayerSync(player)){
            event.setCancelled(true);
        }
    }
}
