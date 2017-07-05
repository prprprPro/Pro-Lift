package cn.szzxol.pro.lift;

import static cn.szzxol.pro.lift.Elevator.PlayerLift;
import static cn.szzxol.pro.lift.Lift.Speed;
import static cn.szzxol.pro.lift.Lift.StandardDistance;
import static cn.szzxol.pro.lift.Lift.StandardFloor;
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
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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

    public HashMap<Integer, Floor> FloorMap = new HashMap<>();
    private List<Entity> MovingEntity = new LinkedList<>();
    public Base base = new Base();
    public int FloorFrom = 0;
    public int FloorTo = 0;
    public Player player;
    public HashMap<String, Integer> FloorIndex = new HashMap<>();
    public HashMap<Integer, String> FloorStrs = new HashMap<>();

    public Elevator(Player player, Block block) {
        this.player = player;
        Location l = block.getLocation();
        l.setY(l.getBlockY() + 1);
        if (l.getBlock().getType() == Material.SIGN) {
            Sign sign = (Sign) l.getBlock().getState();
            if (!sign.getLine(0).contains("lift")) {
                return;
            }
            String[] msg2 = sign.getLine(1).split(":");
            this.FloorFrom = Integer.valueOf(msg2[1]);
            this.FloorTo = Integer.valueOf(sign.getLine(3));
            if (this.FloorFrom == 0 || this.FloorTo == 0 || this.FloorFrom == this.FloorTo) {
                return;
            }
        } else {
            return;
        }
        do {
            l.setY(l.getBlockY() - 1);
            if (l.getBlockY() <= 0) {
                return;
            }
        } while (l.getBlock().getType() != baseMaterial);
        this.base = new Base(l);
        this.FloorIndex.put(this.base.FloorName, 0);
        this.FloorStrs.put(0, this.base.FloorName);
        PlayerLift.put(player, this.lift());
        for (int i = 1, j = 1; i < StandardHeight && j <= StandardFloor; i++) {
            l.setY(l.getBlockY() + i);
            if (l.getBlock().getType() == floorMaterial) {
                this.FloorMap.put(j, new Floor(this.base, j, l.getBlockY()));
                this.FloorIndex.put(this.FloorMap.get(j).FloorName, j);
                this.FloorStrs.put(j, this.FloorMap.get(j).FloorName);
                j++;
            }
        }
        this.MovingEntity.add(player);
        this.run();
    }

    public void run() {
        if (!this.exist()) {
            return;
        }
        Player player = (Player) this.MovingEntity.get(0);
        this.MovingEntity.addAll(player.getNearbyEntities(StandardDistance * 2, StandardDistance * 2, StandardDistance * 2));
        this.MovingEntity.stream().filter((entity) -> (!isInLift(entity))).forEach((entity) -> {
            this.MovingEntity.remove(entity);
        });
        if (this.MovingEntity.isEmpty()) {
            return;
        }
        for (int i = min(this.FloorFrom, this.FloorTo) + 1; i <= max(this.FloorFrom, this.FloorTo); i++) {
            FloorMap.get(i).disappear();
        }
        this.MovingEntity.stream().forEach((entity) -> {
            Vector vector = new Vector(0, Speed, 0);
            entity.setVelocity(vector);
            if (!DropingEntity.contains(entity)) {
                DropingEntity.add(entity);
            }
        });
        this.stop();
    }

    public void stop() {
        do {
            this.MovingEntity.stream().filter((entity) -> (entity.getLocation().getBlockY() > this.FloorTo + 1 && entity.getLocation().getBlockY() < this.FloorTo + 2)).map((entity) -> {
                entity.setVelocity(new Vector(0, 0, 0));
                return entity;
            }).map((entity) -> {
                entity.setFallDistance(0F);
                return entity;
            }).forEach((entity) -> {
                this.MovingEntity.remove(entity);
            });
        } while (this.MovingEntity.isEmpty());
        for (int i = min(this.FloorFrom, this.FloorTo) + 1; i <= max(this.FloorFrom, this.FloorTo); i++) {
            FloorMap.get(i).appear();
        }
        PlayerLift.remove(this.player);
    }

    public Elevator lift() {
        return this;
    }

    public boolean exist() {
        return !this.FloorMap.isEmpty();
    }

    public boolean isInLift(Entity entity) {
        Location l = entity.getLocation();
        Location location = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
        return this.base.isInArea(location);
    }

    public Elevator(Block block) {
        Sign sign = (Sign) block.getState();
        if (!sign.getLine(0).contains("lift")) {
            return;
        }
        String[] msg2 = sign.getLine(1).split(":");
        this.FloorFrom = Integer.valueOf(msg2[1]);
        this.FloorTo = Integer.valueOf(sign.getLine(3));
        if (this.FloorFrom == 0 || this.FloorTo == 0 || this.FloorFrom == this.FloorTo) {
            return;
        }
        Location l = block.getLocation();
        do {
            l.setY(l.getBlockY() - 1);
            if (l.getBlockY() <= 0) {
                return;
            }
        } while (l.getBlock().getType() != baseMaterial);
        this.base = new Base(l);
        this.FloorIndex.put(this.base.FloorName, 0);
        this.FloorStrs.put(0, this.base.FloorName);
        for (int i = 1, j = 1; i < StandardHeight && j <= StandardFloor; i++) {
            l.setY(l.getBlockY() + i);
            if (l.getBlock().getType() == floorMaterial) {
                this.FloorMap.put(j, new Floor(this.base, j, l.getBlockY()));
                this.FloorIndex.put(this.FloorMap.get(j).FloorName, j);
                this.FloorStrs.put(j, this.FloorMap.get(j).FloorName);
                j++;
            }
        }
    }

    public String getFloor(Entity entity) {
        Double height = entity.getLocation().getY();
        if (height > this.base.center.getBlockY() && height <= FloorMap.get(1).Height) {
            return this.base.FloorName;
        }
        int bound = FloorMap.get(1).Height;
        for (int i = 2; i <= FloorMap.size(); i++) {
            if (height > bound && height <= FloorMap.get(i).Height) {
                return FloorMap.get(i - 1).FloorName;
            }
        }
        return "不存在的楼层";
    }
}
