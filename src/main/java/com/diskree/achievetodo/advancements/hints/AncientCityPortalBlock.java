package com.diskree.achievetodo.advancements.hints;

import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Objects;

public class AncientCityPortalBlock extends Block {

    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;

    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    private boolean hideParticles;
    private float particlesSpeedMultiplier;

    public AncientCityPortalBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
    }

    public void impulseParticles() {
        particlesSpeedMultiplier = 60.0f;
        hideParticles = false;
    }

    public void hideParticles() {
        particlesSpeedMultiplier = 1.0f;
        hideParticles = true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (Objects.requireNonNull(state.get(AXIS)) == Direction.Axis.Z) {
            return Z_SHAPE;
        }
        return X_SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = state.get(AXIS);
        if (axis2 != axis && axis.isHorizontal() || neighborState.isOf(this) || neighborState.isOf(Blocks.REINFORCED_DEEPSLATE)) {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
        return Blocks.AIR.getDefaultState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient || entity == null || entity.isRemoved()) {
            return;
        }
        if (entity instanceof ItemEntity dragonEgg && dragonEgg.getStack().isOf(Items.DRAGON_EGG)) {
            Entity owner = dragonEgg.getOwner();
            if (owner instanceof ServerPlayerEntity player) {
                AncientCityPortalEntity portalEntity = AncientCityPortalEntity.findForBlock(world, pos);
                if (portalEntity != null && portalEntity.grantDragonEgg(player)) {
                    AchieveToDo.grantHintsAdvancement(player, "three_pointer");
                    dragonEgg.kill();
                }
            }
        } else if (entity instanceof AncientCityPortalExperienceOrbEntity xpOrb) {
            AncientCityPortalEntity portalEntity = AncientCityPortalEntity.findForBlock(world, pos);
            if (portalEntity != null && portalEntity.charge()) {
                xpOrb.kill();
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (hideParticles) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            if (particlesSpeedMultiplier > 1.0f) {
                particlesSpeedMultiplier -= 0.02f;
            }
            double d = (double) pos.getX() + random.nextDouble();
            double e = (double) pos.getY() + random.nextDouble();
            double f = (double) pos.getZ() + random.nextDouble();
            double g = ((double) random.nextFloat() - 0.5) * 0.5 * particlesSpeedMultiplier;
            double h = ((double) random.nextFloat() - 0.5) * 0.5 * particlesSpeedMultiplier;
            double j = ((double) random.nextFloat() - 0.5) * 0.5 * particlesSpeedMultiplier;
            int k = random.nextInt(2) * 2 - 1;
            if (world.getBlockState(pos.west()).isOf(this) || world.getBlockState(pos.east()).isOf(this)) {
                f = (double) pos.getZ() + 0.5 + 0.25 * (double) k;
                j = random.nextFloat() * 2.0f * (float) k;
            } else {
                d = (double) pos.getX() + 0.5 + 0.25 * (double) k;
                g = random.nextFloat() * 2.0f * (float) k;
            }
            world.addParticle(AchieveToDo.ANCIENT_CITY_PORTAL_PARTICLES, d, e, f, g, h, j);
        }
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        if (Objects.requireNonNull(rotation) == BlockRotation.COUNTERCLOCKWISE_90 || rotation == BlockRotation.CLOCKWISE_90) {
            Direction.Axis axis = state.get(AXIS);
            if (Objects.requireNonNull(axis) == Direction.Axis.X) {
                return state.with(AXIS, Direction.Axis.Z);
            }
            if (axis == Direction.Axis.Z) {
                return state.with(AXIS, Direction.Axis.X);
            }
            return state;
        }
        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canBucketPlace(BlockState state, Fluid fluid) {
        return false;
    }
}
