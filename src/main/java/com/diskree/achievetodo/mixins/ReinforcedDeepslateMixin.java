package com.diskree.achievetodo.mixins;

import com.diskree.achievetodo.advancements.hints.AncientCityPortalEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class ReinforcedDeepslateMixin {

    @Unique
    private boolean isReinforcedDeepslate() {
        Block block = (Block) (Object) this;
        try {
            return AncientCityPortalEntity.isReinforcedDeepslate(block.getHardness(), block.getBlastResistance());
        } catch (Throwable t) {
            return false;
        }
    }

    @Shadow
    public abstract BlockState getDefaultState();

    @Shadow
    protected abstract void setDefaultState(BlockState state);

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initReturnInject(AbstractBlock.Settings settings, CallbackInfo ci) {
        if (isReinforcedDeepslate()) {
            setDefaultState(getDefaultState()
                    .with(AncientCityPortalEntity.REINFORCED_DEEPSLATE_CHARGED_PROPERTY, false)
                    .with(AncientCityPortalEntity.REINFORCED_DEEPSLATE_BROKEN_PROPERTY, false)
            );
        }
    }

    @Inject(method = "appendProperties", at = @At("RETURN"))
    protected void appendPropertiesInject(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (isReinforcedDeepslate()) {
            builder.add(AncientCityPortalEntity.REINFORCED_DEEPSLATE_CHARGED_PROPERTY);
            builder.add(AncientCityPortalEntity.REINFORCED_DEEPSLATE_BROKEN_PROPERTY);
        }
    }
}
