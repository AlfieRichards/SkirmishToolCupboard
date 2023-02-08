package me.skirmish.handlers;

import com.google.common.base.Strings;
import me.skirmish.SkirmishTC;
import me.skirmish.objects.TC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WorldHandler implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
        final Player p = e.getPlayer();
        final Block block = e.getBlock();
        Location placedBlockLocation = block.getLocation();
        int[] placedCords = new int[]{placedBlockLocation.getBlockX(),placedBlockLocation.getBlockY(),placedBlockLocation.getBlockZ()};

        if (e.getBlock().getType().equals(Material.EMERALD_BLOCK) && p.getLocation().getWorld().getBlockAt(placedCords[0],placedCords[1]-1,placedCords[2]).getType() == Material.SMOOTH_BRICK){
            if (getNearbyTC(p, 30) != null) {
                e.setCancelled(true);
                p.sendMessage("You can't place a TC so close to another one!");
                return;
            }
            System.out.println(e.getBlock().getLocation());
            SkirmishTC.allTCs.add(new TC(e.getBlock().getLocation()));
            //makes sure tcs are saved
            SkirmishTC.saveTcs();
            p.sendMessage("You have placed a new TC!");
            return;
        }
        final TC tc = getNearbyTC(p, 30);
        if (tc == null || e.getBlock().getType().equals(Material.STEP) || tc.getAccessedPlayers().contains(p.getUniqueId().toString())) return;
        e.setCancelled(true);
        p.sendMessage("You are not currently accessed to the nearby TC!");
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        final Player p = e.getPlayer();
        final TC tc = getNearbyTC(p, 30);
        if (e.getBlock().getType().equals(Material.EMERALD_BLOCK)) {
            for (TC t : SkirmishTC.allTCs) {
                if (t.getLocation().equals(e.getBlock().getLocation())) {
                    SkirmishTC.allTCs.remove(t);
                    //makes sure tcs are saved
                    SkirmishTC.saveTcs();
                    p.sendMessage("You have broken a TC!");
                    return;
                }
            }
            return;
        }
        if (tc == null || tc.getAccessedPlayers().contains(p.getUniqueId().toString())) return;
        e.setCancelled(true);
        p.sendMessage("You are not currently accessed to the nearby TC!");
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand().equals(EquipmentSlot.OFF_HAND) || e.getClickedBlock() == null || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
        final Player p = e.getPlayer();
        if (e.getClickedBlock().getType().equals(Material.EMERALD_BLOCK)) {
            TC tc = null;
            for (TC t : SkirmishTC.allTCs) {
                System.out.println("tc loc " + t.getLocation());
                System.out.println("clicked loc " + e.getClickedBlock().getLocation());
                System.out.println("are equal:" + t.getLocation().equals(e.getClickedBlock().getLocation()));
                if (t.getLocation().equals(e.getClickedBlock().getLocation())) {
                    tc = t;
                    break;
                }
            }
            if (tc == null) {
                p.sendMessage("Internal error occurred: TC not found!");
                return;
            }
            if (tc.getAccessCode().equals("")) {
                Inventory inv = Bukkit.createInventory(null, 9, "TC | What do you want to do?");
                ItemStack authorise = new ItemStack(Material.EMERALD_BLOCK);
                ItemMeta meta = authorise.getItemMeta();
                meta.setDisplayName("Authorise");
                authorise.setItemMeta(meta);
                ItemStack clearSelfAuth = new ItemStack(Material.REDSTONE_BLOCK);
                meta = clearSelfAuth.getItemMeta();
                meta.setDisplayName("Clear self authorisation");
                clearSelfAuth.setItemMeta(meta);
                ItemStack clearAllAuth = new ItemStack(Material.REDSTONE_BLOCK);
                meta = clearAllAuth.getItemMeta();
                meta.setDisplayName("Clear all authorisations");
                clearAllAuth.setItemMeta(meta);
                ItemStack book = new ItemStack(Material.BOOK);
                meta = book.getItemMeta();
                Location l = e.getClickedBlock().getLocation();
                meta.setDisplayName(l.getBlockX()+" "+l.getBlockY()+" "+l.getBlockZ());
                book.setItemMeta(meta);
                inv.setItem(0, authorise);
                inv.setItem(1, clearSelfAuth);
                inv.setItem(2, clearAllAuth);
                inv.setItem(8, book);
                p.openInventory(inv);
                return;
            }
            //makes sure tcs are saved
            SkirmishTC.saveTcs();
            return;
        }
        final TC tc = getNearbyTC(p, 30);
        if (tc == null || tc.getAccessedPlayers().contains(p.getUniqueId().toString())) return;
        boolean hasOnline = false;
        for (String uuid : tc.getAccessedPlayers()) {
            if (SkirmishTC.plugin.getServer().getPlayer(uuid) != null) {
                hasOnline = true;
                //makes sure tcs are saved
                SkirmishTC.saveTcs();
                break;
            }
        }
        if (!hasOnline) {
            e.setCancelled(true);
            p.sendMessage("You are not currently accessed to the nearby TC and no players are online!");
            return;
        }
        if (e.getClickedBlock().getType().equals(Material.CHEST))
            p.sendMessage("You have accessed a container in a claim you do not own!");
    }

    private TC getNearbyTC(Player p, int radius) {
        for (TC tc : SkirmishTC.allTCs) {
            if (p.getLocation().distance(tc.getLocation()) <= radius)
                return tc;
        }
        return null;
    }
}
