package cn.szzxol.pro.lift;

import static cn.szzxol.pro.lift.Elevator.DropingEntity;
import java.util.HashMap;
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

/**
 *
 * @author I_promise
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent evt) {
        Player player = evt.getPlayer();
        if (evt.getAction() == Action.RIGHT_CLICK_BLOCK || evt.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = evt.getClickedBlock();
            if ((block.getType() == Material.WOOD_BUTTON || block.getType() == Material.STONE_BUTTON) && evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Elevator e = new Elevator(player, block);
            } else if (block.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) block.getState();
                player.sendMessage(sign.getLine(0));
                if (sign.getLine(0).contains("lift")) {
                    Elevator e = new Elevator(block);
                    sign.setLine(1, "当前楼层:" + e.getFloor(player));
                    sign.setLine(2, "↓ 目标楼层 ↓");
                    sign.setLine(3, e.FloorStrs.get(e.FloorIndex.get(sign.getLine(3)) + 1 > e.FloorMap.size() ? 0 : e.FloorIndex.get(sign.getLine(3)) + 1));
                    sign.update();
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
    public void SignChange(SignChangeEvent evt) {
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
