package ru.khasanova.weatherhh.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.*;
import java.util.List;

import ru.khasanova.weatherhh.data.base.City;

/**
 * Created by Анжелика on 03.05.2017.
 */

public class CitiesWeather {

    @SerializedName("list")
    @Expose
    List<City> cities = new LinkedList<>();

    public List<City> getCities() {
        return cities;
    }

    public void setCities(java.util.List<City> cities) {
        this.cities = cities;
    }

    public void addCity(City city){
        cities.add(city);
    }

    public int getSize(){
        return cities.size();
    }
}
