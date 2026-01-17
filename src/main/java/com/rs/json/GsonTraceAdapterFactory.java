package com.rs.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class GsonTraceAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value != null) {
                    System.out.println("[GSON WRITE] " + value.getClass().getName());
                }
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                System.out.println("[GSON READ ] " + type.getRawType().getName());
                return delegate.read(in);
            }
        };
    }
}
