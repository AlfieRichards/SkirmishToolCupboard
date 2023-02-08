package me.skirmish.handlers;

import me.skirmish.SkirmishTC;
import me.skirmish.objects.TC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GUIHandler implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (!title.equals("TC | What do you want to do?")) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
        String[] loc = e.getInventory().getItem(8).getItemMeta().getDisplayName().split(" ");
        Location location = new Location(Bukkit.getWorld("world"), Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
        for (TC tc : SkirmishTC.allTCs) {
            if (tc.getLocation().equals(location)) {
                String clickedItemName = e.getCurrentItem().getItemMeta().getDisplayName();
                Player p = (Player)e.getWhoClicked();

                if (clickedItemName.equals("Authorise")) {
                    if (tc.getAccessedPlayers().contains(p.getUniqueId().toString())) {
                        p.sendMessage("You are already authorised to this TC!");
                        return;
                    }
                    tc.getAccessedPlayers().add(p.getUniqueId().toString());
                    //makes sure tcs are saved
                    SkirmishTC.saveTcs();
                    p.sendMessage("You have authorised yourself to the TC!");
                    p.closeInventory();
                    return;
                }

                if (clickedItemName.equals("Clear self authorisation")) {
                    tc.getAccessedPlayers().remove(p.getUniqueId().toString());
                    //makes sure tcs are saved
                    SkirmishTC.saveTcs();
                    p.sendMessage("You have removed your auth!");
                    p.closeInventory();
                    return;
                }

                if (clickedItemName.equals("Clear all authorisations")) {
                    tc.getAccessedPlayers().clear();
                    //makes sure tcs are saved
                    SkirmishTC.saveTcs();
                    p.sendMessage("You have removed all auths!");
                    p.closeInventory();
                    return;
                }

                break;
            }
        }
    }
}
