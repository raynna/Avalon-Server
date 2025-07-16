package com.rs.json;

import com.google.gson.*;
import com.rs.java.game.item.meta.ChargeData;
import com.rs.java.game.item.meta.ItemMetadata;

import java.lang.reflect.Type;

public class ItemMetadataDeserializer implements JsonDeserializer<ItemMetadata> {

    private static final java.util.Map<String, Class<? extends ItemMetadata>> TYPE_MAP = new java.util.HashMap<>();

    static {
        TYPE_MAP.put("dfs", ChargeData.class);
    }

    @Override
    public ItemMetadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        JsonElement typeElement = obj.get("type");
        if (typeElement == null) {
            throw new JsonParseException("Missing 'type' field in ItemMetadata: " + obj.toString());
        }

        String type = typeElement.getAsString().toLowerCase();

        Class<? extends ItemMetadata> clazz = TYPE_MAP.get(type);
        if (clazz == null) {
            throw new JsonParseException("Unknown ItemMetadata type: " + type);
        }

        return context.deserialize(json, clazz);
    }
}
