package cn.szzxol.pro.lift;

import static cn.szzxol.pro.lift.Lift.Speed;
import static cn.szzxol.pro.lift.Lift.StandardDistance;
import static cn.szzxol.pro.lift.Lift.StandardHeight;
import static cn.szzxol.pro.lift.Lift.baseMaterial;
import static cn.szzxol.pro.lift.Lift.floorMaterial;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author I_promise
 */
public class Elevator {

    public static HashMap<Player, Elevator> PlayerLift = new HashMap<>();
    public static List<Entity> DropingEntity = new LinkedList<>();

    public int[] floorY = new int[100];
    private Location center;
    private final List<Location> Base = new LinkedList<>();
    private final List<Location>[] Area = new List[100];
    private final List<Entity> MovingEntity = new LinkedList<>();
    public int FloorFrom = 0;
    public int FloorTo = 1;
    public boolean RightConfig;

    public Elevator(Player player, Block block) {
        if (block.getType() == Material.WALL_SIGN) {
            Location l = block.getLocation();
            l.setY(l.getBlockY() - 1);
            block = (l.getBlock().getType() == Material.WOOD_BUTTON || l.getBlock().getType() == Material.STONE_BUTTON) ? l.getBlock() : null;
        }
        boolean result;
        result = this.initial(block);
        if (result) {
            PlayerLift.put(player, this);
            this.RightConfig = true;
        } else {
            this.RightConfig = false;
        }
    }

    public boolean isConfig() {
        return this.RightConfig;
    }

    public boolean isUP() {
        return FloorFrom < FloorTo;
    }

    private boolean initial(Block button) {
        for (int i = 1; i < StandardHeight; i++) {
            Location l = button.getLocation();
            World world = l.getWorld();
            int x = l.getBlockX();
            int y = l.getBlockY() - i;
            int z = l.getBlockZ();
            Location location = new Location(world, x, y, z);
            if (location.getBlock().getType() == baseMaterial) {
                this.center = location;
                this.floorY[0] = location.getBlockY();
                if (this.setBase(location) == false) {
                    return false;
                }
                this.setFloor(location);
                this.Area[0] = Base;
                return true;
            }
        }
        return false;
    }

    private boolean setBase(Location l) {
        World world = l.getWorld();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        Location location = new Location(world, x, y, z);
        if (location.getBlock().getType() != baseMaterial) {
            return false;
        }
        this.findBaseArea(world, x, y, z);
        return true;
    }

    private void findBaseArea(World world, int x, int y, int z) {
        Location location = new Location(world, x, y, z);
        if (location.getBlock().getType() == baseMaterial) {
            if (Base.contains(location)) {
                return;
            }
            Base.add(location);
            if (isInDistance(location)) {
                this.findBaseArea(world, x - 1, y, z);
                this.findBaseArea(world, x + 1, y, z);
                this.findBaseArea(world, x, y, z - 1);
                this.findBaseArea(world, x, y, z + 1);
            }
        }
    }

    private void setFloor(Location l) {
        World world = l.getWorld();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        int floor = 0;
        for (int i = 1; i < StandardHeight; i++) {
            Location location = new Location(world, x, y + i, z);
            if (location.getBlock().getType() == floorMaterial) {
                floor++;
                this.floorY[floor] = y + i;
                this.findFloorArea(floor, i);
            }
        }
    }

    private void findFloorArea(int floor, int delta) {
        List<Location> L = new LinkedList<>();
        for (Location l : Base) {
            l.setY(this.center.getBlockY() + delta);
            if (l.getBlock().getType() == floorMaterial) {
                L.add(l);
            }
        }
        this.Area[floor] = L;
    }

    private boolean isInDistance(Location location) {
        return this.center.distance(location) < StandardDistance;
    }

    private boolean isInLift(Location location) {
        if (this.Area == null) {
            return false;
        }
        for (Location l : this.Area[0]) {
            if (location.getBlockX() == l.getBlockX() && location.getBlockZ() == l.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    public void run(Player player) {
        List<Entity> Entities = new LinkedList<>();
        Entities.add((Entity) player);
        Entities.addAll(player.getNearbyEntities(StandardDistance * 2, StandardDistance * 2, StandardDistance * 2));
        for (Entity entity : Entities) {
            if (isInLift(entity.getLocation())) {
                this.MovingEntity.add(entity);
            }
        }
        if (this.MovingEntity.isEmpty()) {
            return;
        }
        for (int i = min(this.FloorFrom, this.FloorTo) + 1; i <= max(this.FloorFrom, this.FloorTo); i++) {
            for (Location location : this.Area[i]) {
                location.getBlock().setType(Material.AIR);
            }
        }
        this.MovingEntity.stream().forEach((entity) -> {
            Vector vector = new Vector(0, Speed, 0);
            entity.setVelocity(vector);
        });
    }

    public void stop(Player player) {
        for (int i = min(this.FloorFrom, this.FloorTo) + 1; i <= max(this.FloorFrom, this.FloorTo); i++) {
            for (Location location : this.Area[i]) {
                location.getBlock().setType(floorMaterial);
            }
        }
        for (Entity entity : this.MovingEntity) {
            if (!DropingEntity.contains(entity)) {
                DropingEntity.add(entity);
            }
            entity.setVelocity(new Vector(0, 0, 0));
            entity.setFallDistance(0F);
        }
        PlayerLift.remove(player);
    }
}
