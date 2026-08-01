package com.example.lghpatches;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 這個 mod 本身沒有任何遊戲內容，純粹是拿來承載 Mixin。
 * 所有實際效果都由 mixin 資料夾底下的兩個 Mixin 類別完成。
 */
@Mod("lgh_patches")
public class LghPatchesMod {

    private static final Logger LOGGER = LogManager.getLogger();

    public LghPatchesMod() {
        LOGGER.info("[lgh_patches] Mixin 補丁已載入：Village Recruits 的內建預設建築機率已歸零（若該類別有自訂結構）。");
    }
}
