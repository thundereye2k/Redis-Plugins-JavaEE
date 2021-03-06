package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.UHC;
import me.javaee.uhc.handlers.Scenario;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class AbsorptionLessListener implements Listener {

    @EventHandler
    public void onEat(final PlayerItemConsumeEvent event) {
        if (Scenario.getByName("AbsorptionLess").isEnabled()) {
            if (event.getItem().getType() == Material.GOLDEN_APPLE) {

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);
                    }
                }.runTaskLaterAsynchronously(UHC.getInstance(), 1L);
            }
        }
    }
}
