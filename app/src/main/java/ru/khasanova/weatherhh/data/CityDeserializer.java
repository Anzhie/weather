package ru.khasanova.weatherhh.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ru.khasanova.weatherhh.data.base.City;

/**
 * Created by Анжелика on 03.05.2017.
 */

public class CityDeserializer implements JsonDeserializer<City> {
    @Override
    public City deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        City city = new City();
        city.setTime(System.currentTimeMillis());
        city.setName(jsonObject.get("name").getAsString());

        Main main = context.deserialize(jsonObject.get("main").getAsJsonObject(), Main.class);
        city.setTemp(main.getTemp().toString());

        JsonArray weatherArray = jsonObject.get("weather").getAsJsonArray();
        for (JsonElement elementW : weatherArray) {
            Weather weather = context.deserialize(elementW, Weather.class);
            city.setDescription(weather.getDescription());
        }

        return city;
    }
}