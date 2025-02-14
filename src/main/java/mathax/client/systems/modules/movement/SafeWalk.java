package mathax.client.systems.modules.movement;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.entity.player.ClipAtLedgeEvent;
import mathax.client.events.world.CollisionShapeEvent;
import mathax.client.settings.BlockListSetting;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.block.*;
import net.minecraft.item.Items;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;

public class SafeWalk extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> ledge = sgGeneral.add(new BoolSetting.Builder()
        .name("ledge")
        .description("Prevents you from walking of blocks, like pressing shift.")
        .defaultValue(true)
        .build()
    );

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("Which blocks to prevent on walking")
        .filter(this::blockFilter)
        .build()
    );

    private final Setting<Boolean> magma = sgGeneral.add(new BoolSetting.Builder()
        .name("magma")
        .description("Prevents you from walking over magma blocks.")
        .defaultValue(false)
        .build()
    );

    public SafeWalk() {
        super(Categories.Movement, Items.GOLDEN_BOOTS, "safe-walk", "Prevents you from walking off or on specified blocks.");
    }

    @EventHandler
    private void onClipAtLedge(ClipAtLedgeEvent event) {
        if (!mc.player.isSneaking()) event.setClip(ledge.get());
    }

    @EventHandler
    private void onCollisionShape(CollisionShapeEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (event.type != CollisionShapeEvent.CollisionType.BLOCK) return;
        if (blocks.get().contains(event.state.getBlock())) event.shape = VoxelShapes.fullCube();
        else if (magma.get() && !mc.player.isSneaking()
            && event.state.isAir()
            && mc.world.getBlockState(event.pos.down()).getBlock() == Blocks.MAGMA_BLOCK) {
            event.shape = VoxelShapes.fullCube();
        }
    }

    private boolean blockFilter(Block block) {
        if (block instanceof AbstractFireBlock) return true;
        if (block instanceof AbstractPressurePlateBlock) return true;
        if (block instanceof TripwireBlock) return true;
        if (block instanceof TripwireHookBlock) return true;
        if (block instanceof CobwebBlock) return true;
        if (block instanceof CampfireBlock) return true;
        if (block instanceof SweetBerryBushBlock) return true;
        if (block instanceof CactusBlock) return true;
        if (block instanceof AbstractRailBlock) return true;
        if (block instanceof TrapdoorBlock) return true;
        if (block instanceof PowderSnowBlock) return true;
        return block instanceof AbstractCauldronBlock;
    }
}
