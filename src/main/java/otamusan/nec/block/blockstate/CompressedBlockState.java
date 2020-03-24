package otamusan.nec.block.blockstate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import otamusan.nec.block.BlockCompressed;
import otamusan.nec.block.tileentity.TileCompressedBlock;
import otamusan.nec.item.ItemCompressed;

public class CompressedBlockState extends BlockState {
	@Nullable
	private CompressedBlockState.Cache cache;
	private final int lightLevel;
	private final boolean field_215709_e;

	public CompressedBlockState(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> properties) {
		super(blockIn, properties);
		this.lightLevel = blockIn.getLightValue(this);
		this.field_215709_e = blockIn.func_220074_n(this);
	}

	@Override
	public float getSlipperiness(IWorldReader world, BlockPos pos, Entity entity) {
		return BlockCompressed.getOriginalState(world, pos).getSlipperiness(world, pos, entity);
	}

	@Override
	public int getLightValue(IBlockReader world, BlockPos pos) {
		return BlockCompressed.getOriginalState(world, pos).getLightValue(world, pos);
	}

	@Override
	public boolean isLadder(IWorldReader world, BlockPos pos, LivingEntity entity) {
		return BlockCompressed.getOriginalState(world, pos).isLadder(world, pos, entity);
	}

	@Override
	public float getExplosionResistance(IWorldReader world, BlockPos pos, Entity exploder, Explosion explosion) {
		return BlockCompressed.getOriginalState(world, pos).getExplosionResistance(world, pos, exploder, explosion);
	}

	@Override
	public boolean canConnectRedstone(IBlockReader world, BlockPos pos, Direction side) {
		return BlockCompressed.getOriginalState(world, pos).canConnectRedstone(world, pos, side);
	}

	@Override
	public ItemStack getPickBlock(RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return BlockCompressed.getOriginalItem(world, pos).copy();
	}

	@Override
	public int getExpDrop(IWorldReader world, BlockPos pos, int fortune, int silktouch) {
		return (int) (BlockCompressed.getOriginalState(world, pos).getExpDrop(world, pos, fortune, silktouch)
				* Math.pow(8, ItemCompressed.getTime(BlockCompressed.getOriginalItem(world, pos))));
	}

	@Override
	public float getEnchantPowerBonus(IWorldReader world, BlockPos pos) {
		return (float) (BlockCompressed.getOriginalState(world, pos).getEnchantPowerBonus(world, pos)
				* Math.pow(8, ItemCompressed.getTime(BlockCompressed.getOriginalItem(world, pos))));
	}

	@Override
	public SoundType getSoundType(IWorldReader world, BlockPos pos, Entity entity) {
		return BlockCompressed.getOriginalState(world, pos).getSoundType(world, pos, entity);
	}

	@Override
	public Vec3d getFogColor(IWorldReader world, BlockPos pos, Entity entity, Vec3d originalColor, float partialTicks) {
		return BlockCompressed.getOriginalState(world, pos).getFogColor(world, pos, entity, originalColor,
				partialTicks);
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return BlockCompressed.getOriginalState(world, pos).getFlammability(world, pos, face);
	}

	@Override
	public boolean isFlammable(IBlockReader world, BlockPos pos, Direction face) {
		return BlockCompressed.getOriginalState(world, pos).isFlammable(world, pos, face);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return BlockCompressed.getOriginalState(world, pos).getFireSpreadSpeed(world, pos, face);
	}

	@Override
	public boolean isFireSource(IBlockReader world, BlockPos pos, Direction side) {
		return BlockCompressed.getOriginalState(world, pos).isFireSource(world, pos, side);
	}

	public void func_215692_c() {
		if (!this.getBlock().isVariableOpacity()) {
			this.cache = new CompressedBlockState.Cache(this);
		}

	}

	public Block getBlock() {
		return this.object;
	}

	public Material getMaterial() {
		return this.getBlock().getMaterial(this);
	}

	public boolean canEntitySpawn(IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
		return BlockCompressed.getOriginalState(worldIn, pos).canEntitySpawn(worldIn, pos, type);
	}

	public boolean propagatesSkylightDown(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).propagatesSkylightDown(worldIn, pos);
	}

	public int getOpacity(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getOpacity(worldIn, pos);
	}

	public VoxelShape func_215702_a(IBlockReader worldIn, BlockPos pos, Direction directionIn) {
		return BlockCompressed.getOriginalState(worldIn, pos).func_215702_a(worldIn, pos, directionIn);
	}

	public boolean func_215704_f() {
		return this.cache == null || this.cache.isCollisionShapeLargerThanFullBlock;
	}

	public boolean func_215691_g() {
		return this.field_215709_e;
	}

	public int getLightValue() {
		return this.lightLevel;
	}

	/** @deprecated use {@link BlockState#isAir(IBlockReader, BlockPos) */
	@Deprecated
	public boolean isAir() {
		return this.getBlock().isAir(this);
	}

	/** @deprecated use {@link BlockState#rotate(IWorld, BlockPos, Rotation) */
	@Deprecated
	public MaterialColor getMaterialColor(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getMaterialColor(worldIn, pos);
	}

	/**
	 * Returns the blockstate with the given rotation. If inapplicable, returns itself.
	 */
	public BlockState rotate(Rotation rot) {
		return this.getBlock().rotate(this, rot);
	}

	/**
	 * Returns the blockstate mirrored in the given way. If inapplicable, returns itself.
	 */
	public BlockState mirror(Mirror mirrorIn) {
		return this.getBlock().mirror(this, mirrorIn);
	}

	public BlockRenderType getRenderType() {
		return this.getBlock().getRenderType(this);
	}

	@OnlyIn(Dist.CLIENT)
	public boolean func_227035_k_() {
		return this.getBlock().func_225543_m_(this);
	}

	@OnlyIn(Dist.CLIENT)
	public float func_215703_d(IBlockReader reader, BlockPos pos) {
		return BlockCompressed.getOriginalState(reader, pos).func_215703_d(reader, pos);
	}

	public boolean isNormalCube(IBlockReader reader, BlockPos pos) {
		return BlockCompressed.getOriginalState(reader, pos).isNormalCube(reader, pos);
	}

	public boolean canProvidePower() {
		return this.getBlock().canProvidePower(this);
	}

	public int getWeakPower(IBlockReader blockAccess, BlockPos pos, Direction side) {
		return BlockCompressed.getOriginalState(blockAccess, pos).getWeakPower(blockAccess, pos, side);
	}

	public boolean hasComparatorInputOverride() {
		return this.getBlock().hasComparatorInputOverride(this);
	}

	public int getComparatorInputOverride(World worldIn, BlockPos pos) {
		return this.getBlock().getComparatorInputOverride(this, worldIn, pos);
	}

	public float getBlockHardness(IBlockReader worldIn, BlockPos pos) {
		return this.getBlock().getBlockHardness(this, worldIn, pos);
	}

	public float getPlayerRelativeBlockHardness(PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		BlockState state2 = BlockCompressed.getOriginalState(worldIn, pos);
		float f = state2.getBlockHardness(worldIn, pos);
		if (f == -1.0F) {
			return 0.0F;
		} else {
			int i = net.minecraftforge.common.ForgeHooks.canHarvestBlock(state2, player, worldIn, pos) ? 30 : 100;
			float c = (float) Math.pow(8, ItemCompressed.getTime(BlockCompressed.getOriginalItem(worldIn, pos)));
			if (((TileCompressedBlock) worldIn.getTileEntity(pos)).isNatural) {
				return player.getDigSpeed(state2, pos) / f / (float) i / c;

			} else {
				return player.getDigSpeed(state2, pos) / f / (float) i;
			}
		}
	}

	public int getStrongPower(IBlockReader blockAccess, BlockPos pos, Direction side) {
		return BlockCompressed.getOriginalState(blockAccess, pos).getStrongPower(blockAccess, pos, side);
	}

	public PushReaction getPushReaction() {
		return this.getBlock().getPushReaction(this);
	}

	public boolean isOpaqueCube(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).isOpaqueCube(worldIn, pos);
	}

	public boolean isSolid() {
		if (!this.has(BlockCompressed.PRO_SIDERENDER))
			return true;
		return this.get(BlockCompressed.PRO_SIDERENDER).booleanValue();
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isSideInvisible(BlockState state, Direction face) {
		return this.getBlock().isSideInvisible(this, state, face);
	}

	public VoxelShape getShape(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getShape(worldIn, pos);
	}

	public VoxelShape getShape(IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BlockCompressed.getOriginalState(worldIn, pos).getShape(worldIn, pos, context);
	}

	public VoxelShape getCollisionShape(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getCollisionShape(worldIn, pos);
	}

	public VoxelShape getCollisionShape(IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BlockCompressed.getOriginalState(worldIn, pos).getCollisionShape(worldIn, pos, context);
	}

	public VoxelShape getRenderShape(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getRenderShape(worldIn, pos);
	}

	public VoxelShape getRaytraceShape(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getRaytraceShape(worldIn, pos);
	}

	/*public final boolean func_215682_a(IBlockReader reader, BlockPos pos, Entity entityIn) {
		return Block.doesSideFillSquare(this.getCollisionShape(reader, pos, ISelectionContext.forEntity(entityIn)),
				Direction.UP);
	}*/

	public Vec3d getOffset(IBlockReader access, BlockPos pos) {
		return BlockCompressed.getOriginalState(access, pos).getOffset(access, pos);
	}

	/**
	 * Called on both Client and Server when World#addBlockEvent is called. On the Server, this may perform additional
	 * changes to the world, like pistons replacing the block with an extended base. On the client, the update may
	 * involve replacing tile entities, playing sounds, or performing other visual actions to reflect the server side
	 * changes.
	 */
	public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param) {
		return this.getBlock().eventReceived(this, worldIn, pos, id, param);
	}

	public void neighborChanged(World worldIn, BlockPos p_215697_2_, Block blockIn, BlockPos p_215697_4_,
			boolean isMoving) {
		this.getBlock().neighborChanged(this, worldIn, p_215697_2_, blockIn, p_215697_4_, isMoving);
	}

	/**
	 * For all neighbors, have them react to this block's existence, potentially updating their states as needed. For
	 * example, fences make their connections to this block if possible and observers pulse if this block was placed in
	 * front of their detector
	 */
	public void updateNeighbors(IWorld worldIn, BlockPos pos, int flags) {
		this.getBlock().updateNeighbors(this, worldIn, pos, flags);
	}

	/**
	 * Performs validations on the block state and possibly neighboring blocks to validate whether the incoming state is
	 * valid to stay in the world. Currently used only by redstone wire to update itself if neighboring blocks have
	 * changed and to possibly break itself.
	 */
	public void updateDiagonalNeighbors(IWorld worldIn, BlockPos pos, int flags) {
		this.getBlock().updateDiagonalNeighbors(this, worldIn, pos, flags);
	}

	public void onBlockAdded(World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		this.getBlock().onBlockAdded(this, worldIn, pos, oldState, isMoving);
	}

	public void onReplaced(World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		this.getBlock().onReplaced(this, worldIn, pos, newState, isMoving);
	}

	public void func_227033_a_(ServerWorld p_227033_1_, BlockPos p_227033_2_, Random p_227033_3_) {
		this.getBlock().func_225534_a_(this, p_227033_1_, p_227033_2_, p_227033_3_);
	}

	public void func_227034_b_(ServerWorld p_227034_1_, BlockPos p_227034_2_, Random p_227034_3_) {
		this.getBlock().func_225542_b_(this, p_227034_1_, p_227034_2_, p_227034_3_);
	}

	public void onEntityCollision(World worldIn, BlockPos pos, Entity entityIn) {
		this.getBlock().onEntityCollision(this, worldIn, pos, entityIn);
	}

	public void spawnAdditionalDrops(World worldIn, BlockPos pos, ItemStack stack) {
		this.getBlock().spawnAdditionalDrops(this, worldIn, pos, stack);
	}

	public List<ItemStack> getDrops(LootContext.Builder builder) {
		return this.getBlock().getDrops(this, builder);
	}

	public ActionResultType func_227031_a_(World p_227031_1_, PlayerEntity p_227031_2_, Hand p_227031_3_,
			BlockRayTraceResult p_227031_4_) {
		return this.getBlock().func_225533_a_(this, p_227031_1_, p_227031_4_.getPos(), p_227031_2_, p_227031_3_,
				p_227031_4_);
	}

	public void onBlockClicked(World worldIn, BlockPos pos, PlayerEntity player) {
		this.getBlock().onBlockClicked(this, worldIn, pos, player);
	}

	public boolean func_229980_m_(IBlockReader p_229980_1_, BlockPos p_229980_2_) {
		return BlockCompressed.getOriginalState(p_229980_1_, p_229980_2_).func_229980_m_(p_229980_1_, p_229980_2_);
	}

	@OnlyIn(Dist.CLIENT)
	public boolean causesSuffocation(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).causesSuffocation(worldIn, pos);
	}

	public BlockState updatePostPlacement(Direction face, BlockState queried, IWorld worldIn, BlockPos currentPos,
			BlockPos offsetPos) {
		return this.getBlock().updatePostPlacement(this, face, queried, worldIn, currentPos, offsetPos);
	}

	public boolean allowsMovement(IBlockReader worldIn, BlockPos pos, PathType type) {
		return this.getBlock().allowsMovement(this, worldIn, pos, type);
	}

	public boolean isReplaceable(BlockItemUseContext useContext) {
		return BlockCompressed.getOriginalState(useContext.getWorld(), useContext.getPos()).isReplaceable(useContext);
	}

	public boolean func_227032_a_(Fluid p_227032_1_) {
		return this.getBlock().func_225541_a_(this, p_227032_1_);
	}

	public boolean isValidPosition(IWorldReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).isValidPosition(worldIn, pos);
	}

	public boolean blockNeedsPostProcessing(IBlockReader worldIn, BlockPos pos) {
		return this.getBlock().needsPostProcessing(this, worldIn, pos);
	}

	@Nullable
	public INamedContainerProvider getContainer(World worldIn, BlockPos pos) {
		return this.getBlock().getContainer(this, worldIn, pos);
	}

	public boolean isIn(Tag<Block> tagIn) {
		return this.getBlock().isIn(tagIn);
	}

	public IFluidState getFluidState() {
		return this.getBlock().getFluidState(this);
	}

	public boolean ticksRandomly() {
		return this.getBlock().ticksRandomly(this);
	}

	@OnlyIn(Dist.CLIENT)
	public long getPositionRandom(BlockPos pos) {
		return this.getBlock().getPositionRandom(this, pos);
	}

	public SoundType getSoundType() {
		return this.getBlock().getSoundType(this);
	}

	public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, Entity projectile) {
		this.getBlock().onProjectileCollision(worldIn, state, hit, projectile);
	}

	public boolean func_224755_d(IBlockReader p_224755_1_, BlockPos p_224755_2_, Direction p_224755_3_) {
		return BlockCompressed.getOriginalState(p_224755_1_, p_224755_2_).func_224755_d(p_224755_1_, p_224755_2_,
				p_224755_3_);
	}

	public boolean func_224756_o(IBlockReader p_224756_1_, BlockPos p_224756_2_) {
		return BlockCompressed.getOriginalState(p_224756_1_, p_224756_2_).func_224756_o(p_224756_1_, p_224756_2_);
	}

	public static <T> BlockState deserialize(Dynamic<T> dynamic) {
		Block block = Registry.BLOCK.getOrDefault(new ResourceLocation(
				dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:air")));
		Map<String, String> map = dynamic.get("Properties").asMap((p_215701_0_) -> {
			return p_215701_0_.asString("");
		}, (p_215694_0_) -> {
			return p_215694_0_.asString("");
		});
		BlockState blockstate = block.getDefaultState();
		StateContainer<Block, BlockState> statecontainer = block.getStateContainer();

		for (Entry<String, String> entry : map.entrySet()) {
			String s = entry.getKey();
			IProperty<?> iproperty = statecontainer.getProperty(s);
			if (iproperty != null) {
				blockstate = IStateHolder.func_215671_a(blockstate, iproperty, s, dynamic.toString(), entry.getValue());
			}
		}

		return blockstate;
	}

	static final class Cache {
		private static final Direction[] DIRECTIONS = Direction.values();
		private final boolean solid;
		private final boolean opaqueCube;
		private final boolean propagatesSkylightDown;
		private final int opacity;
		private final VoxelShape[] renderShapes;
		private final VoxelShape field_230026_g;
		private final boolean isCollisionShapeLargerThanFullBlock;
		private final boolean[] field_225493_i;
		private final boolean field_225494_j;

		private Cache(BlockState stateIn) {
			Block block = stateIn.getBlock();
			this.solid = block.isSolid(stateIn);
			this.opaqueCube = block.isOpaqueCube(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
			this.propagatesSkylightDown = block.propagatesSkylightDown(stateIn, EmptyBlockReader.INSTANCE,
					BlockPos.ZERO);
			this.opacity = block.getOpacity(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
			if (!stateIn.isSolid()) {
				this.renderShapes = null;
			} else {
				this.renderShapes = new VoxelShape[DIRECTIONS.length];
				VoxelShape voxelshape = block.getRenderShape(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO);

				for (Direction direction : DIRECTIONS) {
					this.renderShapes[direction.ordinal()] = VoxelShapes.func_216387_a(voxelshape, direction);
				}
			}

			this.field_230026_g = block.getCollisionShape(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO,
					ISelectionContext.dummy());
			this.isCollisionShapeLargerThanFullBlock = Arrays.stream(Direction.Axis.values())
					.anyMatch((p_222491_1_) -> {
						return this.field_230026_g.getStart(p_222491_1_) < 0.0D
								|| this.field_230026_g.getEnd(p_222491_1_) > 1.0D;
					});
			this.field_225493_i = new boolean[6];

			for (Direction direction1 : DIRECTIONS) {
				this.field_225493_i[direction1.ordinal()] = Block.hasSolidSide(stateIn, EmptyBlockReader.INSTANCE,
						BlockPos.ZERO, direction1);
			}

			this.field_225494_j = Block.isOpaque(stateIn.getCollisionShape(EmptyBlockReader.INSTANCE, BlockPos.ZERO));
		}
	}
}