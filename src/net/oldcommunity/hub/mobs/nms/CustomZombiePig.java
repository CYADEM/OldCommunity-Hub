package net.oldcommunity.hub.mobs.nms;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;

import java.lang.reflect.Field;

public class CustomZombiePig extends EntityPigZombie {

    public CustomZombiePig(World world) {
        super(world);
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(this.goalSelector, new UnsafeList());
            bField.set(this.targetSelector, new UnsafeList());
            cField.set(this.goalSelector, new UnsafeList());
            cField.set(this.targetSelector, new UnsafeList());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void g(double x, double y, double z) {
    }

    protected String z() {
        return "";
    }

    protected String bo() {
        return "";
    }

    protected String bp() {
        return "";
    }

    protected void a(BlockPosition blockposition, Block block) {
    }

    public void m() {
    }
}
