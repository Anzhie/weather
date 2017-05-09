package ru.khasanova.weatherhh.network;

import android.support.annotation.NonNull;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Анжелика on 22.04.2017.
 */

public interface ServiceInterface {
    //получаем данные для группы городов
    @GET("data/2.5/group")      //find
    //получаем погоду, подставляя параметры
    Call<ResponseBody> getWeatherFromOWM(@NonNull @QueryMap Map<String, String> params);
}
