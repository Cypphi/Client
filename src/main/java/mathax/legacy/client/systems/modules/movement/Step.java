package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.events.entity.player.PlayerMoveEvent;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.world.Timer;
import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.utils.player.PlayerUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.shape.VoxelShape;

public class Step extends Module {
    private float prevStepHeight;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines how to bypass.")
        .defaultValue(Mode.NCP)
        .build()
    );

    private final Setting<ActiveWhen> activeWhen = sgGeneral.add(new EnumSetting.Builder<ActiveWhen>()
        .name("active-when")
        .description("Step is active when you meet these requirements.")
        .defaultValue(ActiveWhen.Always)
        .build()
    );

    private final Setting<Double> height = sgGeneral.add(new DoubleSetting.Builder()
        .name("step-height")
        .description("Your step height.")
        .defaultValue(1)
        .range(1, 2.5)
        .sliderRange(1, 2.5)
        .build()
    );

    private final Setting<Boolean> timer = sgGeneral.add(new BoolSetting.Builder()
        .name("timer")
        .description("Whether or not to use a timer.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder()
        .name("only-on-ground")
        .description("Whether or not to step even if you are in the air.")
        .defaultValue(true)
        .build()
    );

    public Step() {
        super(Categories.Movement, Items.GOLDEN_BOOTS, "step", "Allows you to walk up full blocks.");
    }

    @Override
    public void onActivate() {
        prevStepHeight = mc.player.stepHeight;
    }

    @Override
    public void onDeactivate() {
        mc.player.stepHeight = prevStepHeight;
        Modules.get().get(Timer.class).setOverride(Timer.OFF);
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if ((activeWhen.get() == ActiveWhen.Sneaking && !mc.player.isSneaking()) || (activeWhen.get() == ActiveWhen.Not_Sneaking && mc.player.isSneaking()) || (!mc.player.isOnGround() && onlyOnGround.get())) return;
        if (mode.get() == Mode.NCP_PLUS) mc.player.stepHeight = height.get().floatValue();
        if (mode.get() == Mode.Normal) {
            mc.player.stepHeight = height.get().floatValue();
            return;
        }

        if (!timer.get()) Modules.get().get(Timer.class).setOverride(Timer.OFF);

        double[] dir = PlayerUtils.directionSpeed(0.1f);

        // One Block

        if (shouldStep(dir, 1.0, 0.6) && height.get() >= 1.0){
            for (double y : new double[] {0.42, 0.753}) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + y, mc.player.getZ(), mc.player.isOnGround()));
            }

            if (timer.get()) Modules.get().get(Timer.class).setOverride(1.6);
            mc.player.setPosition(mc.player.getX(), mc.player.getY() + 1.0, mc.player.getZ());
        } else Modules.get().get(Timer.class).setOverride(Timer.OFF);

        // Tall One Block
        if (shouldStep(dir, 1.6, 1.4) && height.get() >= 1.5){
            for (double y : new double[] {0.42, 0.75, 1.0, 1.16, 1.23, 1.2}) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + y, mc.player.getZ(), mc.player.isOnGround()));
            }

            if (timer.get()) Modules.get().get(Timer.class).setOverride(1.35);
            mc.player.setPosition(mc.player.getX(), mc.player.getY() + 1.5, mc.player.getZ());
        } else Modules.get().get(Timer.class).setOverride(Timer.OFF);

        if (shouldStep(dir, 2.1, 1.9) && height.get() >= 2.0){
            for (double y : new double[] {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43}) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + y, mc.player.getZ(), mc.player.isOnGround()));
            }

            if (timer.get()) Modules.get().get(Timer.class).setOverride(1.25);
            mc.player.setPosition(mc.player.getX(), mc.player.getY() + 2.0, mc.player.getZ());
        } else Modules.get().get(Timer.class).setOverride(Timer.OFF);

        if (shouldStep(dir, 2.6, 2.4) && height.get() >= 2.5){
            for (double y : new double[] {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907}) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + y, mc.player.getZ(), mc.player.isOnGround()));
            }

            if (timer.get()) Modules.get().get(Timer.class).setOverride(1.15);
            mc.player.setPosition(mc.player.getX(), mc.player.getY() + 2.5, mc.player.getZ());
        } else Modules.get().get(Timer.class).setOverride(Timer.OFF);
    }

    private boolean shouldStep(double[] dir, double y1, double y2) {
        return !getCollisions(dir[0], y1, dir[1]).iterator().hasNext() && getCollisions(dir[0], y2, dir[1]).iterator().hasNext();
    }

    private Iterable<VoxelShape> getCollisions(double x, double y, double z) {
        return (Iterable<VoxelShape>) mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(x, y, z));
    }

    public enum Mode {
        Normal,
        NCP,
        NCP_PLUS;

        @Override
        public String toString() {
            return super.toString().replace("_PLUS", "+");
        }
    }

    public enum ActiveWhen {
        Always,
        Sneaking,
        Not_Sneaking;

        @Override
        public String toString() {
            return super.toString().replace("_", " ");
        }
    }
}
