package com.example.lghpatches.mixin;

import com.example.villagerecruits.structures.workers.WorkerStructureProvider;
import com.example.villagerecruits.structures.workers.WorkersDefaultStructures;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

/**
 * 原本 WorkerStructureProvider.choose() 的邏輯是：
 *   pool = 你的自訂房屋 + 你的自訂工作站 + 1個BARRACKS
 *   builtins = WorkersDefaultStructures.forBiome(biome);  // mod內建的醜房屋，永遠無條件加入
 *   total = pool.size() + builtins.size();
 *   隨機從 0~total 均勻抽一個 index
 *
 * 這個 Mixin 攔截「取得內建清單」那一次呼叫：
 *   - 如果這個 biome 已經湊出的自訂候選池 (pool) 不是空的 -> 回傳空清單，
 *     內建的醜房屋/工作站直接出局，機率變成真正的 0%。
 *   - 如果自訂候選池是空的（例如某個 biome 你完全沒做房屋）-> 照原本邏輯回傳內建清單當保底。
 *
 * 需要 MixinExtras 的 @Local 來讀取原方法裡已經組好的 pool 區域變數。
 */
@Mixin(WorkerStructureProvider.class)
public class WorkerStructureProviderMixin {

    @Redirect(
        method = "choose",
        at = @At(
            value = "INVOKE",
            target = "Lcom/example/villagerecruits/structures/workers/WorkersDefaultStructures;forBiome(Ljava/lang/String;)Ljava/util/List;"
        )
    )
    private static List<CompoundTag> lgh$skipBuiltinIfCustomExists(
            String biome,
            @Local(ordinal = 0) List<ResourceLocation> pool) {

        if (pool != null && !pool.isEmpty()) {
            return Collections.emptyList();
        }
        return WorkersDefaultStructures.forBiome(biome);
    }
}
