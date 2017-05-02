package ru.khasanova.weatherhh.network;

import android.support.annotation.NonNull;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import ru.khasanova.weatherhh.data.Cities;

/**
 * Created by Анжелика on 22.04.2017.
 */

public interface ServiceInterface {
    //получаем данные для группы городов
    @GET("data/2.5/group")      //find
    //получаем погоду, подставляя параметры
    Call<Cities> getWeatherFromOWM(@NonNull @QueryMap Map<String, String> params);
}
