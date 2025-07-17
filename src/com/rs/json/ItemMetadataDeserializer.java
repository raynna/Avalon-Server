package com.rs.json;

import com.google.gson.*;
import com.rs.java.game.item.meta.DragonFireShieldMetaData;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.item.meta.MetaDataType;
import com.rs.java.game.item.meta.RunePouchMetaData;

import java.lang.reflect.Type;

public class ItemMetadataDeserializer implements JsonDeserializer<ItemMetadata> {

    private static final java.util.Map<Integer, Class<? extends ItemMetadata>> TYPE_MAP = new java.util.HashMap<>();

    static {
        TYPE_MAP.put(MetaDataType.DRAGONFIRE_SHIELD.getId(), DragonFireShieldMetaData.class);
        TYPE_MAP.put(MetaDataType.RUNE_POUCH.getId(), RunePouchMetaData.class);
    }

    @Override
    public ItemMetadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        JsonElement typeElement = obj.get("type");
        if (typeElement == null) {
            throw new JsonParseException("Missing 'type' field in ItemMetadata: " + obj.toString());
        }

        int type = typeElement.getAsInt();

        Class<? extends ItemMetadata> clazz = TYPE_MAP.get(type);
        if (clazz == null) {
            throw new JsonParseException("Unknown ItemMetadata type: " + type);
        }

        return context.deserialize(json, clazz);
    }
}
