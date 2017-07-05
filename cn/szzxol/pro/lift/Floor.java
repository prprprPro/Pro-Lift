package cn.szzxol.pro.lift;

import static cn.szzxol.pro.lift.Lift.floorMaterial;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

/**
 *
 * @author I_promise
 */
public class Floor {

    private final List<Location> LocationList = new LinkedList<>();
    public String FloorName;
    public int Height;

    public Floor(Base base, int floor, int height) {
        base.getLocationList().stream().map((location) -> {
            location.setY(height);
            return location;
        }).filter((location) -> (location.getBlock().getType() == floorMaterial)).forEach((location) -> {
            this.LocationList.add(location);
        });
        this.Height = height;
        Location l = base.center;
        l.setY(height + 1);
        if (l.getBlock().getType() == Material.SIGN) {
            Sign sign = (Sign) l.getBlock().getState();
            this.FloorName = sign.getLine(0);
        } else {
            this.FloorName = String.valueOf(floor);
        }
    }

    public List<Location> getLocationList() {
        return this.LocationList;
    }

    public void appear() {
        this.LocationList.stream().forEach((location) -> {
            location.getBlock().setType(floorMaterial);
        });
    }

    public void disappear() {
        this.LocationList.stream().forEach((location) -> {
            location.getBlock().setType(Material.AIR);
        });
    }

}
