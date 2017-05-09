package ru.khasanova.weatherhh.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.khasanova.weatherhh.data.CitiesDeserializer;
import ru.khasanova.weatherhh.data.CitiesWeather;
import ru.khasanova.weatherhh.data.CityDeserializer;
import ru.khasanova.weatherhh.data.base.City;

/**
 * Created by Анжелика on 22.04.2017.
 */

public class NetService extends IntentService{

    private static final String TAG         = "WeatherHH";
    private static final String CITY_KEY    = "city_key";
    public static final String RESULT       = "ru.khasanova.weatherhh.backend.NetService.REQUEST_PROCESSED";

    public List<City> cities;
    Realm realm;

    LocalBroadcastManager broadcastManager;

    public NetService(){
        super(NetService.class.getName());
    }

    @Override
    public void onCreate(){
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public static void start(@NonNull Context context, @NonNull String groupCities){
        //стартуем service, в качестве параметра передаем список городов, для которых надо загрузить погоду
        Intent intent = new Intent(context, NetService.class);
        intent.putExtra(CITY_KEY, groupCities);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent){
        //извлекаем список городов, для которых надо загрузить погоду
        String groupCities = intent.getStringExtra(CITY_KEY);

        //создаем GSON
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(CitiesWeather.class, new CitiesDeserializer())
                .registerTypeAdapter(City.class, new CityDeserializer())
                .create();

        //создаем ретрофит:базовый адрес + GSON
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                //.addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //для ретрофита есть интерфейс ServiceInterface, где определяется тип запроса и параметры
        ServiceInterface service = retrofit.create(ServiceInterface.class);

        //делаем запрос погоды асинхронно
        Call<ResponseBody> call = service.getWeatherFromOWM(createParams(groupCities));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //если вернулся 200й код - получен ответ по запросу
                if (response.isSuccessful()) {
                    //записываем данные погоды по городам в БД
                    try {
                        final String body = response.body().string();
                        final CitiesWeather citiesWeather = gson.fromJson(body, CitiesWeather.class);

                        realm = Realm.getInstance(NetService.this);
                        realm.beginTransaction();
                        realm.copyToRealm(citiesWeather.getCities());
                        realm.commitTransaction();
                        Log.e(TAG, "success");

                        //возвращаемся в UI
                        Intent intentBack = new Intent(RESULT);
                        broadcastManager.sendBroadcast(intentBack);

                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }
                else {
                    Log.e(TAG, "problem: loadWeather response");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "loadWeather failure");
            }
        });
    }


    private Map<String, String> createParams(String groupCities){
        //параметры запроса погоды: для группы городов, в метрической системе, на 1 день, указываем свой api
        Map<String, String> hashMap = new HashMap<>();

        hashMap.put("id", groupCities);
        hashMap.put("type", "like");
        hashMap.put("units", "metric");
        hashMap.put("cnt", "1");
        hashMap.put("appid", "8ff8c29207a244b7cd7a0d63357defb0");

        return hashMap;
    }

}
