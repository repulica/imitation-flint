package repulica.imitationflint;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.OperatorBlock;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ImitationFlintItem extends Item {
	public ImitationFlintItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		PlayerEntity player = context.getPlayer();
		if (!world.isClient && player != null) {
			if (lightBlock(world, pos, player) && !player.isCreative()) {
				context.getStack().damage(1, player, playerx -> playerx.sendToolBreakStatus(context.getHand()));
			}
			return ActionResult.SUCCESS;
		}
		return super.useOnBlock(context);
	}

	public static boolean lightBlock(World world, BlockPos pos, @Nullable PlayerEntity player) {
		BlockState state = world.getBlockState(pos);
		if (state.isIn(ImitationFlint.EXPLOSION_DISALLOWED)) return false;
		TntEntity tnt = new TntEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player);
		tnt.method_54455(state);
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
		world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
		world.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		world.emitGameEvent(player, GameEvent.PRIME_FUSE, pos);
		world.spawnEntity(tnt);
		return true;
	}
}
