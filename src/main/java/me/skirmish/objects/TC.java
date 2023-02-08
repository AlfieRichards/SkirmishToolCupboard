package me.skirmish.objects;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TC implements ConfigurationSerializable {

    static String name;
    private final Location location;
    private final List<String> accessed;
    private String accessCode;


    public TC(String name, Location location, List<String> accessed, String accessCode) {
        this.name = "name";
        this.location = location;
        this.accessed = accessed;
        this.accessCode = accessCode;
    }

    public TC(Location location){
        this.name = "name";
        this.location = location;
        this.accessed = new ArrayList<>();
        this.accessCode = "";
    }

    public Location getLocation() {
        return location;
    }

    public List<String> getAccessedPlayers() {
        return accessed;
    }

    public void addAccessedPlayer(OfflinePlayer op) {
        accessed.add(op.getUniqueId().toString());
    }

    public void clearAccessedPlayers() {
        accessed.clear();
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("name", name);
        //AAAAAAAAAAAAAAAAAAAAAA
        System.out.println(location);
        map.put("location", location);
        map.put("accessed", accessed);
        map.put("accessCode", accessCode);
        return map;
    }


    public static TC deserialize(Map<String, Object> map) {
        TC tc = new TC((String)map.get("name"), (Location)map.get("location"), (List<String>)map.get("accessed"), (String)map.get("accessCode"));
        TC.name = (String) map.get("name");
        return tc;
    }
}
