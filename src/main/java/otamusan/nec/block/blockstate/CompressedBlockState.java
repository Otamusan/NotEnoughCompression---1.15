package otamusan.nec.block.blockstate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

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
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
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

/*
 * The compressed block uses this BlockState
 */
public class CompressedBlockState extends BlockState {
	@Nullable
	private CompressedBlockState.Cache cache;
	private final int lightLevel;
	private final boolean field_215709_e;

	public CompressedBlockState(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> properties) {
		super(blockIn, properties);
		this.lightLevel = blockIn.getLightValue(this);
		this.field_215709_e = blockIn.isTransparent(this);
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

	@Override

	public void cacheState() {
		if (!this.getBlock().isVariableOpacity()) {
			this.cache = new CompressedBlockState.Cache(this);
		}

	}

	@Override

	public Block getBlock() {
		return this.object;
	}

	@Override

	public Material getMaterial() {
		return this.getBlock().getMaterial(this);
	}

	@Override

	public boolean canEntitySpawn(IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
		return BlockCompressed.getOriginalState(worldIn, pos).canEntitySpawn(worldIn, pos, type);
	}

	@Override

	public boolean propagatesSkylightDown(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).propagatesSkylightDown(worldIn, pos);
	}

	@Override

	public int getOpacity(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getOpacity(worldIn, pos);
		//return Blocks.TALL_GRASS.getDefaultState().getOpacity(worldIn, pos);
	}

	@Override

	public VoxelShape getFaceOcclusionShape(IBlockReader worldIn, BlockPos pos, Direction directionIn) {

		//return Blocks.TALL_GRASS.getDefaultState().getFaceOcclusionShape(worldIn, pos, directionIn);
		return BlockCompressed.getOriginalState(worldIn, pos).getFaceOcclusionShape(worldIn, pos, directionIn);
	}

	@Override

	public boolean isCollisionShapeLargerThanFullBlock() {
		return this.cache == null || this.cache.isCollisionShapeLargerThanFullBlock;
	}

	@Override

	public boolean isTransparent() {
		//return this.field_215709_e
		return true;
	}

	@Override

	public int getLightValue() {
		return this.lightLevel;
	}

	@Override

	/** @deprecated use {@link BlockState#isAir(IBlockReader, BlockPos) */
	@Deprecated
	public boolean isAir() {
		return this.getBlock().isAir(this);
	}

	@Override

	/** @deprecated use {@link BlockState#rotate(IWorld, BlockPos, Rotation) */
	@Deprecated
	public MaterialColor getMaterialColor(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getMaterialColor(worldIn, pos);
	}

	@Override

	/**
	 * Returns the blockstate with the given rotation. If inapplicable, returns itself.
	 */
	public BlockState rotate(Rotation rot) {
		return this.getBlock().rotate(this, rot);
	}

	@Override

	/**
	 * Returns the blockstate mirrored in the given way. If inapplicable, returns itself.
	 */
	public BlockState mirror(Mirror mirrorIn) {
		return this.getBlock().mirror(this, mirrorIn);
	}

	@Override

	public BlockRenderType getRenderType() {
		return this.getBlock().getRenderType(this);
	}

	@Override

	@OnlyIn(Dist.CLIENT)
	public boolean isEmissiveRendering() {
		return this.getBlock().isEmissiveRendering(this);
	}

	@Override

	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(IBlockReader reader, BlockPos pos) {
		return BlockCompressed.getOriginalState(reader, pos).getAmbientOcclusionLightValue(reader, pos);
	}

	@Override

	public boolean isNormalCube(IBlockReader reader, BlockPos pos) {
		return BlockCompressed.getOriginalState(reader, pos).isNormalCube(reader, pos);
	}

	@Override

	public boolean canProvidePower() {
		return this.getBlock().canProvidePower(this);
	}

	@Override

	public int getWeakPower(IBlockReader blockAccess, BlockPos pos, Direction side) {
		return BlockCompressed.getOriginalState(blockAccess, pos).getWeakPower(blockAccess, pos, side);
	}

	@Override

	public boolean hasComparatorInputOverride() {
		return this.getBlock().hasComparatorInputOverride(this);
	}

	@Override

	public int getComparatorInputOverride(World worldIn, BlockPos pos) {
		return this.getBlock().getComparatorInputOverride(this, worldIn, pos);
	}

	@Override

	public float getBlockHardness(IBlockReader worldIn, BlockPos pos) {
		return this.getBlock().getBlockHardness(this, worldIn, pos);
	}

	@Override

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

	@Override

	public int getStrongPower(IBlockReader blockAccess, BlockPos pos, Direction side) {
		return BlockCompressed.getOriginalState(blockAccess, pos).getStrongPower(blockAccess, pos, side);
	}

	@Override

	public PushReaction getPushReaction() {
		return this.getBlock().getPushReaction(this);
	}

	@Override
	public boolean isOpaqueCube(IBlockReader worldIn, BlockPos pos) {
		return true;
		//return BlockCompressed.getOriginalState(worldIn, pos).isOpaqueCube(worldIn, pos);
	}

	@Override
	public boolean isSolid() {
		if (!this.has(BlockCompressed.PRO_SIDERENDER))
			return true;
		return this.get(BlockCompressed.PRO_SIDERENDER).booleanValue();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isSideInvisible(BlockState state, Direction face) {
		return this.getBlock().isSideInvisible(this, state, face);
	}

	@Override

	public VoxelShape getShape(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getShape(worldIn, pos);
	}

	@Override

	public VoxelShape getShape(IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BlockCompressed.getOriginalState(worldIn, pos).getShape(worldIn, pos, context);
	}

	@Override

	public VoxelShape getCollisionShape(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getCollisionShape(worldIn, pos);
	}

	@Override

	public VoxelShape getCollisionShape(IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BlockCompressed.getOriginalState(worldIn, pos).getCollisionShape(worldIn, pos, context);
	}

	@Override

	public VoxelShape getRenderShape(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).getRenderShape(worldIn, pos);
	}

	@Override

	public VoxelShape getRaytraceShape(IBlockReader worldIn, BlockPos pos) {
		//return Blocks.TALL_GRASS.getDefaultState().getRaytraceShape(worldIn, pos);

		return BlockCompressed.getOriginalState(worldIn, pos).getRaytraceShape(worldIn, pos);
	}

	/*public final boolean func_215682_a(IBlockReader reader, BlockPos pos, Entity entityIn) {
		return Block.doesSideFillSquare(this.getCollisionShape(reader, pos, ISelectionContext.forEntity(entityIn)),
				Direction.UP);
	}*/
	@Override

	public Vec3d getOffset(IBlockReader access, BlockPos pos) {
		return BlockCompressed.getOriginalState(access, pos).getOffset(access, pos);
	}

	/**
	 * Called on both Client and Server when World#addBlockEvent is called. On the Server, this may perform additional
	 * changes to the world, like pistons replacing the block with an extended base. On the client, the update may
	 * involve replacing tile entities, playing sounds, or performing other visual actions to reflect the server side
	 * changes.
	 */
	@Override

	public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param) {
		return this.getBlock().eventReceived(this, worldIn, pos, id, param);
	}

	@Override

	public void neighborChanged(World worldIn, BlockPos p_215697_2_, Block blockIn, BlockPos p_215697_4_,
			boolean isMoving) {
		this.getBlock().neighborChanged(this, worldIn, p_215697_2_, blockIn, p_215697_4_, isMoving);
	}

	/**
	 * For all neighbors, have them react to this block's existence, potentially updating their states as needed. For
	 * example, fences make their connections to this block if possible and observers pulse if this block was placed in
	 * front of their detector
	 */
	@Override

	public void updateNeighbors(IWorld worldIn, BlockPos pos, int flags) {
		this.getBlock().updateNeighbors(this, worldIn, pos, flags);
	}

	/**
	 * Performs validations on the block state and possibly neighboring blocks to validate whether the incoming state is
	 * valid to stay in the world. Currently used only by redstone wire to update itself if neighboring blocks have
	 * changed and to possibly break itself.
	 */
	@Override

	public void updateDiagonalNeighbors(IWorld worldIn, BlockPos pos, int flags) {
		this.getBlock().updateDiagonalNeighbors(this, worldIn, pos, flags);
	}

	@Override

	public void onBlockAdded(World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		this.getBlock().onBlockAdded(this, worldIn, pos, oldState, isMoving);
	}

	@Override

	public void onReplaced(World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		this.getBlock().onReplaced(this, worldIn, pos, newState, isMoving);
	}

	@Override

	public void tick(ServerWorld p_227033_1_, BlockPos p_227033_2_, Random p_227033_3_) {
		this.getBlock().tick(this, p_227033_1_, p_227033_2_, p_227033_3_);
	}

	@Override

	public void randomTick(ServerWorld p_227034_1_, BlockPos p_227034_2_, Random p_227034_3_) {
		this.getBlock().randomTick(this, p_227034_1_, p_227034_2_, p_227034_3_);
	}

	@Override

	public void onEntityCollision(World worldIn, BlockPos pos, Entity entityIn) {
		this.getBlock().onEntityCollision(this, worldIn, pos, entityIn);
	}

	@Override

	public void spawnAdditionalDrops(World worldIn, BlockPos pos, ItemStack stack) {
		this.getBlock().spawnAdditionalDrops(this, worldIn, pos, stack);
	}

	@Override

	public List<ItemStack> getDrops(LootContext.Builder builder) {
		return this.getBlock().getDrops(this, builder);
	}

	@Override

	public ActionResultType onBlockActivated(World p_227031_1_, PlayerEntity p_227031_2_, Hand p_227031_3_,
			BlockRayTraceResult p_227031_4_) {
		return this.getBlock().onBlockActivated(this, p_227031_1_, p_227031_4_.getPos(), p_227031_2_, p_227031_3_,
				p_227031_4_);
	}

	@Override

	public void onBlockClicked(World worldIn, BlockPos pos, PlayerEntity player) {
		this.getBlock().onBlockClicked(this, worldIn, pos, player);
	}

	@Override

	public boolean isSuffocating(IBlockReader p_229980_1_, BlockPos p_229980_2_) {
		return BlockCompressed.getOriginalState(p_229980_1_, p_229980_2_).isSuffocating(p_229980_1_, p_229980_2_);
	}

	@Override

	@OnlyIn(Dist.CLIENT)
	public boolean causesSuffocation(IBlockReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).causesSuffocation(worldIn, pos);
	}

	@Override

	public BlockState updatePostPlacement(Direction face, BlockState queried, IWorld worldIn, BlockPos currentPos,
			BlockPos offsetPos) {
		return this.getBlock().updatePostPlacement(this, face, queried, worldIn, currentPos, offsetPos);
	}

	@Override

	public boolean allowsMovement(IBlockReader worldIn, BlockPos pos, PathType type) {
		return this.getBlock().allowsMovement(this, worldIn, pos, type);
	}

	@Override

	public boolean isReplaceable(BlockItemUseContext useContext) {
		return BlockCompressed.getOriginalState(useContext.getWorld(), useContext.getPos()).isReplaceable(useContext);
	}

	@Override

	public boolean isReplaceable(Fluid p_227032_1_) {
		return this.getBlock().isReplaceable(this, p_227032_1_);
	}

	@Override

	public boolean isValidPosition(IWorldReader worldIn, BlockPos pos) {
		return BlockCompressed.getOriginalState(worldIn, pos).isValidPosition(worldIn, pos);
	}

	@Override

	public boolean blockNeedsPostProcessing(IBlockReader worldIn, BlockPos pos) {
		return this.getBlock().needsPostProcessing(this, worldIn, pos);
	}

	@Override

	@Nullable
	public INamedContainerProvider getContainer(World worldIn, BlockPos pos) {
		return this.getBlock().getContainer(this, worldIn, pos);
	}

	@Override

	public boolean isIn(Tag<Block> tagIn) {
		return this.getBlock().isIn(tagIn);
	}

	@Override

	public IFluidState getFluidState() {
		return this.getBlock().getFluidState(this);
	}

	@Override

	public boolean ticksRandomly() {
		return this.getBlock().ticksRandomly(this);
	}

	@Override

	@OnlyIn(Dist.CLIENT)
	public long getPositionRandom(BlockPos pos) {
		return this.getBlock().getPositionRandom(this, pos);
	}

	@Override

	public SoundType getSoundType() {
		return this.getBlock().getSoundType(this);
	}

	@Override

	public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, Entity projectile) {
		this.getBlock().onProjectileCollision(worldIn, state, hit, projectile);
	}

	@Override

	public boolean isSolidSide(IBlockReader p_224755_1_, BlockPos p_224755_2_, Direction p_224755_3_) {
		return BlockCompressed.getOriginalState(p_224755_1_, p_224755_2_).isSolidSide(p_224755_1_, p_224755_2_,
				p_224755_3_);
	}

	@Override

	public boolean isCollisionShapeOpaque(IBlockReader p_224756_1_, BlockPos p_224756_2_) {
		//return BlockCompressed.getOriginalState(p_224756_1_, p_224756_2_).isCollisionShapeOpaque(p_224756_1_,
		//		p_224756_2_);
		return true;
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
					this.renderShapes[direction.ordinal()] = VoxelShapes.getFaceShape(voxelshape, direction);
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