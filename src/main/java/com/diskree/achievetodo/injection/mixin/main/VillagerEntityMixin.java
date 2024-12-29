package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.AchieveToDoMod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

    @Shadow
    public abstract VillagerData getVillagerData();

    @WrapOperation(
        method = "interactMob",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/village/TradeOfferList;isEmpty()Z"
        )
    )
    public boolean lockVillager(
        TradeOfferList tradeOffers,
        @NotNull Operation<Boolean> original,
        @Local(argsOnly = true) PlayerEntity player
    ) {
        return original.call(tradeOffers) ||
            AchieveToDoMod.isAbilityLocked(player, AbilityType.findVillagerTradeAbility(getVillagerData().getProfession()));
    }
}
