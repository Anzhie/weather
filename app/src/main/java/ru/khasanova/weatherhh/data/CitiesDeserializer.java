package ru.khasanova.weatherhh.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;

import ru.khasanova.weatherhh.data.base.City;

/**
 * Created by Анжелика on 03.05.2017.
 */

public class CitiesDeserializer implements JsonDeserializer<CitiesWeather> {
    @Override
    public CitiesWeather deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CitiesWeather citiesWeather = new CitiesWeather();
        JsonObject jsonObject       = json.getAsJsonObject();
        JsonArray jsonArray         = jsonObject.get("list").getAsJsonArray();

        for (JsonElement entry : jsonArray){
            City city = context.deserialize(entry, City.class);
            citiesWeather.addCity(city);
        }

        return citiesWeather;
    }
}
