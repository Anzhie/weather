package ru.khasanova.weatherhh.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.khasanova.weatherhh.data.Cities;
import ru.khasanova.weatherhh.data.base.City;

/**
 * Created by Анжелика on 22.04.2017.
 */

public class NetService extends IntentService{

    private static final String TAG         = "WeatherHH";
    private static final String CITY_KEY    = "city_key";

    public List<Cities> cities;
    Realm realm;

    public NetService(){
        super(NetService.class.getName());
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
        Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaredClass().equals(City.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();

        //создаем ретрофит:базовый адрес + GSON
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //для ретрофита есть интерфейс ServiceInterface, где определяется тип запроса и параметры
        ServiceInterface service = retrofit.create(ServiceInterface.class);

        //делаем запрос погоды асинхронно
        Call<Cities> call = service.getWeatherFromOWM(createParams(groupCities));
        call.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                //если вернулся 200й код - получен ответ по запросу
                if (response.isSuccessful()) {
                    //записываем данные погоды по городам в БД
                    final Cities s = response.body();
                    final JSONArray list = new JSONArray(s.getList());

                    realm = Realm.getInstance(NetService.this);
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.createAllFromJson(City.class, list.toString());
                            Log.e(TAG, "success");
                        }
                    });

                }
                else {
                    Log.e(TAG, "problem: loadWeather response");
                }
            }

            @Override
            public void onFailure(Call<Cities> call, Throwable t) {
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
