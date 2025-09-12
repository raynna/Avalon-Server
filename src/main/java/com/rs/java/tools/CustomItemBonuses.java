package com.rs.java.tools;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.rs.kotlin.game.player.equipment.BonusType;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CustomItemBonuses {
    private static final Map<Integer, int[]> CUSTOM_BONUSES = new HashMap<>();

    static {
        load();
    }

    public static void load() {
        try (Reader reader = Files.newBufferedReader(Paths.get("data/items/custom_item_bonuses.json"))) {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                int itemId = Integer.parseInt(entry.getKey());
                JsonObject obj = entry.getValue().getAsJsonObject();

                int[] bonuses = new int[18]; // fixed size array for all bonuses

                bonuses[0]  = obj.get("stab").getAsInt();
                bonuses[1]  = obj.get("slash").getAsInt();
                bonuses[2]  = obj.get("crush").getAsInt();
                bonuses[3]  = obj.get("magic").getAsInt();
                bonuses[4]  = obj.get("ranged").getAsInt();
                bonuses[5]  = obj.get("stabDefence").getAsInt();
                bonuses[6]  = obj.get("slashDefence").getAsInt();
                bonuses[7]  = obj.get("crushDefence").getAsInt();
                bonuses[8]  = obj.get("magicDefence").getAsInt();
                bonuses[9]  = obj.get("rangeDefence").getAsInt();
                bonuses[10] = obj.get("summoningDefence").getAsInt();
                bonuses[11] = obj.get("absorbMelee").getAsInt();
                bonuses[12] = obj.get("absorbMagic").getAsInt();
                bonuses[13] = obj.get("absorbRange").getAsInt();
                bonuses[14] = obj.get("strength").getAsInt();
                bonuses[15] = obj.get("rangedStrength").getAsInt();
                bonuses[16] = obj.get("prayer").getAsInt();
                bonuses[17] = obj.get("magicDamagePercent").getAsInt();

                CUSTOM_BONUSES.put(itemId, bonuses);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] getBonuses(int itemId) {
        return CUSTOM_BONUSES.get(itemId);
    }
}

