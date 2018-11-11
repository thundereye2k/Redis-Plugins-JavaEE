package me.javaee.uhc.border;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlassListener implements Listener {
    private BorderManager tag;

    public GlassListener(BorderManager combatTag) {
        this.tag = combatTag;
    }

    public static boolean isInBetween(int xone, int xother, int mid) {
        int distance = Math.abs(xone - xother);
        return distance == Math.abs(mid - xone) + Math.abs(mid - xother);
    }

    public static int closestNumber(int from, int... numbers) {
        int distance = Math.abs(numbers[0] - from);
        int idx = 0;
        for (int c = 1; c < numbers.length; c++) {
            int cdistance = Math.abs(numbers[c] - from);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        return numbers[idx];
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location to = event.getTo();
            if ((Math.abs(to.getBlockX()) > this.tag.getBorder()) || (Math.abs(to.getBlockZ()) > this.tag.getBorder())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.GRAY + "You can not pearl to outside of the border");
                return;
            }
            onPlayerMoved(event);
        }
    }

    @EventHandler
    public void onPlayerMoved(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if ((from.getBlockX() != to.getBlockX()) || (to.getBlockZ() != from.getBlockZ())) {
            renderGlass(event.getPlayer(), to, -this.tag.getBorder() - 1, this.tag.getBorder(), -this.tag.getBorder() - 1, this.tag.getBorder());
        }
    }

    public boolean renderGlass(Player player, Location to, int minX, int maxX, int minZ, int maxZ) {
        int closerx = closestNumber(to.getBlockX(), minX, maxX);
        int closerz = closestNumber(to.getBlockZ(), minZ, maxZ);

        boolean updateX = Math.abs(to.getX() - closerx) < 10.0D;
        boolean updateZ = Math.abs(to.getZ() - closerz) < 10.0D;

        if ((!updateX) && (!updateZ)) {
            return false;
        }

        List<Location> toUpdate = new ArrayList<>();
        int y;
        int x;
        Location location;
        if (updateX) {
            for (y = -2; y < 6; ++y) {
                for (x = -4; x < 4; ++x) {
                    if (isInBetween(minZ, maxZ, to.getBlockZ() + x)) {
                        location = new Location(to.getWorld(), closerx, to.getBlockY() + y, to.getBlockZ() + x);
                        if (!toUpdate.contains(location) && !location.getBlock().getType().isOccluding()) {
                            toUpdate.add(location);
                        }
                    }
                }
            }
        }

        if (updateZ) {
            for (y = -2; y < 6; ++y) {
                for (x = -4; x < 4; ++x) {
                    if (isInBetween(minX, maxX, to.getBlockX() + x)) {
                        location = new Location(to.getWorld(), to.getBlockX() + x, to.getBlockY() + y, closerz);
                        if (!toUpdate.contains(location) && !location.getBlock().getType().isOccluding()) {
                            toUpdate.add(location);
                        }
                    }
                }
            }
        }
        this.tag.update(player, toUpdate);
        return !toUpdate.isEmpty();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if ((current != null) && (cursor != null) && (cursor.getType() == Material.POTION) && (current.getType() == Material.POTION) && (current.getDurability() == cursor.getDurability()) && (current.getAmount() >= 2) && (cursor.getAmount() >= 2)) {
            event.setCancelled(true);
        }
    }
}
