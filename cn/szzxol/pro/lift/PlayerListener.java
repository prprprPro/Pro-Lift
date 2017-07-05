package cn.szzxol.pro.lift;

import static cn.szzxol.pro.lift.Elevator.DropingEntity;
import static cn.szzxol.pro.lift.Elevator.PlayerLift;
import static cn.szzxol.pro.lift.Lift.LagProtect;
import static cn.szzxol.pro.lift.Lift.Speed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

/**
 *
 * @author I_promise
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent evt) {
        Player player = evt.getPlayer();
        if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = evt.getClickedBlock();
            if (block.getType() == Material.WOOD_BUTTON || block.getType() == Material.STONE_BUTTON) {
                Elevator e = new Elevator(player, block);
                if (!e.isConfig()) {
                    return;
                }
                e.run(player);
            }
            if (block.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) block.getState();
                player.sendMessage(sign.getLine(0));
                if (sign.getLine(0).contains("lift")) {
                    player.sendMessage(sign.getLine(0));
                    Location location = block.getLocation();
                    location.setY(location.getBlockY() - 2);
                    if (location.getBlock().getType() == Material.WALL_SIGN) {
                        player.sendMessage(sign.getLine(0));
                        sign.setLine(0, "【list】");
                        Sign s = (Sign) location.getBlock().getState();
                        sign.setLine(1, "当前楼层" + s.getLine(0));
                        sign.update();
                    }
                }
            }
        }
    }

    @EventHandler
    public void EntityDrop(EntityDamageEvent evt) {
        Entity entity = evt.getEntity();
        if (evt.getCause() == DamageCause.FALL && DropingEntity.contains(entity)) {
            evt.setCancelled(true);
            DropingEntity.remove(entity);
        }
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        Elevator e = PlayerLift.get(player);
        if (e == null) {
            return;
        }
        int high = e.floorY[e.FloorTo];
        if (player.isOnGround()){
            e.stop(player);
        }
        if (e.isUP()) {
            if (player.getLocation().getY() > high + LagProtect / 10) {
                e.stop(player);
            }
        } else if (player.getLocation().getY() < high + 1 + LagProtect) {
            e.stop(player);
        }
    }

    @EventHandler
    public void PlayerChange(SignChangeEvent evt) {
        Sign sign = (Sign) evt.getBlock().getState();
        if (evt.getLine(0).contains("Lift")) {
            String floor = "ewq";
            Location l = sign.getLocation();
            l.setY(sign.getLocation().getY() - 1);
            if (l.getBlock().getType() == Material.WALL_SIGN) {
                Sign signN = (Sign) l.getBlock().getState();
                if (!"".equals(signN.getLine(0))) {
                    floor = signN.getLine(0);
                }
            }
            sign.setLine(1, "当前楼层：" + floor);
            sign.update();
        }
    }
}
