package com.example.lghpatches.mixin;

import com.example.villagerecruits.structures.production.ProductionStructurePool;
import com.example.villagerecruits.structures.production.ProductionSubtype;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 原本 ProductionStructurePool.keysFor() 的邏輯是：
 *   result.add(subtype.structureKey);   // 內建的 vr_production:xxx，永遠無條件加入
 *   result.addAll(DATAPACK.get(subtype)); // 再把 datapack 自訂的加進去
 *
 * 這個 Mixin 攔截在方法最前面：
 *   - 如果這個 subtype 底下 datapack 已經有自訂結構 -> 直接回傳「只有自訂結構」的清單，
 *     內建的 vr_production:xxx 完全不會出現在候選池，機率變成真正的 0%。
 *   - 如果這個 subtype 完全沒有自訂結構 -> 不取消注入，讓原本方法照舊執行，
 *     退回使用內建預設（避免該生產類型完全沒東西可蓋）。
 */
@Mixin(ProductionStructurePool.class)
public abstract class ProductionStructurePoolMixin {

    @Shadow
    @Final
    private static Map<ProductionSubtype, List<String>> DATAPACK;

    @Inject(method = "keysFor", at = @At("HEAD"), cancellable = true)
    private static void lgh$onlyCustomIfAvailable(
            ProductionSubtype subtype,
            CallbackInfoReturnable<List<String>> cir) {

        if (subtype == null) {
            cir.setReturnValue(new ArrayList<>());
            return;
        }

        List<String> custom = DATAPACK.get(subtype);
        if (custom != null && !custom.isEmpty()) {
            cir.setReturnValue(new ArrayList<>(custom));
        }
        // custom 為空/null 時什麼都不做，讓原方法繼續跑，退回內建預設
    }
}
