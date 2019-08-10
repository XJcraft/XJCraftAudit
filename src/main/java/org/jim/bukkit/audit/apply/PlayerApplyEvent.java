package org.jim.bukkit.audit.apply;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.util.Logs;

import java.util.Arrays;

public class PlayerApplyEvent extends Event {

    private static HandlerList list = new HandlerList();

    private Player applyPlayer;
    private ItemStack[] unaccepts;

    public PlayerApplyEvent(Player p) {
        this.applyPlayer = p;
        unaccepts =
                AuditPlugin.getPlugin().getMaterialAudit().accept(applyPlayer);
        Logs.info(
                p.getName() + " trigger PlayApplyEvent,unaccept armorContents: "
                        + unaccepts.length + ", " + Arrays.toString(unaccepts));
    }

    @Override
    public HandlerList getHandlers() {
        return list;
    }

    public Player getPlayer() {
        return applyPlayer;
    }

    public boolean isArmorAccept() {
        return unaccepts.length == 0;
    }

    public ItemStack[] getUnaccepts() {
        return unaccepts;
    }

    public static HandlerList getHandlerList() {
        return list;
    }
}
