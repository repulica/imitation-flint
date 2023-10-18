package repulica.imitationflint;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupAccessor;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImitationFlint implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("imitationflint");

	public static Item IMITATION_FLINT;
	public static TagKey<Block> EXPLOSION_DISALLOWED;

	@Override
	public void onInitialize() {

		IMITATION_FLINT = Registry.register(Registries.ITEM, new Identifier("imitationflint", "imitation_flint"), new ImitationFlintItem(new Item.Settings().maxDamage(64)));
		EXPLOSION_DISALLOWED = TagKey.of(Registries.BLOCK.getKey(), new Identifier("imitationflint", "explosion_disallowed"));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(new ItemStack(IMITATION_FLINT)));

		DispenserBlock.registerBehavior(IMITATION_FLINT, new FallibleItemDispenserBehavior() {
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.world();
				Direction direction = pointer.state().get(DispenserBlock.FACING);
				BlockPos pos = pointer.pos().offset(direction);
				this.setSuccess(true);
				if (!ImitationFlintItem.lightBlock(world, pos, null)) {
					this.setSuccess(false);
				}

				if (this.isSuccess() && stack.damage(1, world.random, null)) {
					stack.setCount(0);
				}

				return stack;
			}
		});
	}
}