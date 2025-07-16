package com.rs.json;

import com.google.gson.*;
import com.rs.java.game.item.meta.ItemMetadata;

import java.lang.reflect.Type;

public class ItemMetadataSerializer implements JsonSerializer<ItemMetadata> {

    @Override
    public JsonElement serialize(ItemMetadata src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = context.serialize(src).getAsJsonObject();
        obj.addProperty("type", src.getType());
        return obj;
    }
}
