package com.rs.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.rs.java.game.item.meta.*;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemMetadataDeserializer implements JsonDeserializer<ItemMetadata> {

    private static final java.util.Map<Integer, Class<? extends ItemMetadata>> TYPE_MAP = new java.util.HashMap<>();

    static {
        TYPE_MAP.put(MetaDataType.DRAGONFIRE_SHIELD.getId(), DragonFireShieldMetaData.class);
        TYPE_MAP.put(MetaDataType.RUNE_POUCH.getId(), RunePouchMetaData.class);
        TYPE_MAP.put(MetaDataType.DEGRADE_TICKS.getId(), DegradeTicksMetaData.class);
        TYPE_MAP.put(MetaDataType.DEGRADE_HITS.getId(), DegradeHitsMetaData.class);
        TYPE_MAP.put(MetaDataType.POLYPORE.getId(), PolyporeStaffMetaData.class);
        TYPE_MAP.put(MetaDataType.GREATER_RUNIC.getId(), GreaterRunicStaffMetaData.class);
    }

    @Override
    public ItemMetadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();

        Integer typeId = obj.has("type") ? obj.get("type").getAsInt() : null;

        JsonObject data;

        if (obj.has("data")) {
            data = obj.getAsJsonObject("data");
        } else {
            data = obj;
        }

        if (typeId == null) {
            typeId = inferTypeFromData(data);
            if (typeId == null) {
                throw new JsonParseException("Missing 'type' field in ItemMetadata: " + obj);
            }
        }

        MetaDataType type = MetaDataType.fromId(typeId);
        if (type == null) {
            throw new JsonParseException("Unknown ItemMetadata type: " + typeId);
        }

        switch (type) {

            case DRAGONFIRE_SHIELD:
                return new DragonFireShieldMetaData(
                        data.get("charges").getAsInt()
                );

            case RUNE_POUCH:
                RunePouchMetaData rune = new RunePouchMetaData();
                Type mapType = new TypeToken<Map<Integer, Integer>>(){}.getType();

                rune.setValue(context.deserialize(data.get("runes"), mapType));

                return rune;

            case DEGRADE_TICKS:
                return new DegradeTicksMetaData(
                        data.get("charges").getAsInt(),
                        data.get("lastDisplayedPercentage").getAsInt()
                );

            case DEGRADE_HITS:
                return new DegradeHitsMetaData(
                        data.get("charges").getAsInt(),
                        data.get("lastDisplayedPercentage").getAsInt()
                );

            case POLYPORE:
                return new PolyporeStaffMetaData(
                        data.get("charges").getAsInt()
                );

            case GREATER_RUNIC:
                return new GreaterRunicStaffMetaData(
                        data.get("spellId").getAsInt(),
                        data.get("charges").getAsInt()
                );

            default:
                throw new JsonParseException("Unhandled ItemMetadata type: " + type);
        }
    }



    private Integer inferTypeFromData(JsonObject obj) {
        if (obj.has("runes")) return MetaDataType.RUNE_POUCH.getId();
        if (obj.has("spellId")) return MetaDataType.GREATER_RUNIC.getId();
        if (obj.has("charges") && !obj.has("spellId")) return MetaDataType.POLYPORE.getId();
        return null;
    }




}
