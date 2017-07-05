package cn.szzxol.pro.lift;

import static cn.szzxol.pro.lift.Lift.StandardDistance;
import static cn.szzxol.pro.lift.Lift.baseMaterial;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;

/**
 *
 * @author I_promise
 */
public class Base {

    public Location center = null;
    private final List<Location> LocationList = new LinkedList<>();
    public String FloorName = "底层";

    public Base() {
    }

    public Base(Location location) {
        this.center = location;
        this.setBase(location);
    }

    private void setBase(Location l) {
        this.findBaseArea(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
        l.setY(l.getBlockY() + 1);
        if (l.getBlock().getType() == Material.SIGN) {
            Sign sign = (Sign) l.getBlock().getState();
            this.FloorName = sign.getLine(0);
        }
    }

    private void findBaseArea(World world, int x, int y, int z) {
        Location location = new Location(world, x, y, z);
        if (location.getBlock().getType() == baseMaterial) {
            if (this.LocationList.contains(location) || !isInDistance(location)) {
                return;
            }
            this.LocationList.add(location);
            this.findBaseArea(world, x - 1, y, z);
            this.findBaseArea(world, x + 1, y, z);
            this.findBaseArea(world, x, y, z - 1);
            this.findBaseArea(world, x, y, z + 1);
        }
    }

    public List<Location> getLocationList() {
        return this.LocationList;
    }

    public boolean isInArea(Location location) {
        if (LocationList.stream().anyMatch((l) -> (l.getBlockX() == location.getBlockX() && l.getBlockZ() == location.getBlockZ()))) {
            return true;
        }
        return false;
    }

    private boolean isInDistance(Location location) {
        return this.center.distance(location) < StandardDistance;
    }

}
