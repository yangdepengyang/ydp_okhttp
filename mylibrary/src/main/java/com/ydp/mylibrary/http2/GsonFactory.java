package com.ydp.mylibrary.http2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GsonFactory {
    public static final SimpleDateFormat FORMAT_DATE_FULL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            switch (peek) {
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return in.nextInt() != 0;
                case STRING:
                    return Boolean.parseBoolean(in.nextString());
                default:
                    throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
            }
        }
    };

    private static final TypeAdapter<Integer> intAsStringAdapter = new TypeAdapter<Integer>() {

        @Override
        public void write(JsonWriter out, Integer value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Integer read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            switch (peek) {
                case NULL:
                    in.nextNull();
                    return 0;
                case NUMBER:
                    return in.nextInt();
                case STRING:
                    try {
                        return Integer.valueOf(in.nextString());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                default:
                    return 0;
            }
        }
    };

    private static final JsonDeserializer<Date> dateAsStringAdapter = new JsonDeserializer<Date>() {

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json != null) {
                final String jsonString = json.getAsString();
                try {
                    return FORMAT_DATE_FULL.parse(jsonString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final long jsonLong = json.getAsLong();
                try {
                    return new Date(jsonLong);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    };

    public static Gson make() {
        return new GsonBuilder()
                .registerTypeAdapter(RequestResult.class, new ResultJsonDeser())
                .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                .registerTypeAdapter(Integer.class, intAsStringAdapter)
                .registerTypeAdapter(int.class, intAsStringAdapter)
                .registerTypeAdapter(Date.class, dateAsStringAdapter)
                .serializeNulls()
                .create();

    }
}
