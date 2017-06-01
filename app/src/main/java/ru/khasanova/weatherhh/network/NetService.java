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
import ru.khasanova.weatherhh.MainActivity;
import ru.khasanova.weatherhh.data.CitiesDeserializer;
import ru.khasanova.weatherhh.data.CitiesWeather;
import ru.khasanova.weatherhh.data.CityDeserializer;
import ru.khasanova.weatherhh.data.base.City;

/**
 * Created by Анжелика.
 */

public class NetService extends IntentService{

    private static final String TAG         = "WeatherHH";
    private static final String CITY_KEY    = "city_key";
    public static final String RESULT       = "ru.khasanova.weatherhh.backend.NetService.REQUEST_PROCESSED";

    MainActivity staticParam = new MainActivity();

    public List<City> cities;
    Realm realm;

    public static Intent intent;
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
        intent = new Intent(context, NetService.class);
        intent.putExtra(CITY_KEY, groupCities);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(final Intent intent){
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
                Intent intentBack = new Intent(RESULT);
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

                        //упаковываем в интент сообщение об успешной записи данных в базу
                        intentBack.putExtra(staticParam.RES_EXC, staticParam.OK);


                    }catch (IOException e){
                        //упаковываем в интент сообщение об ошибке при записи в realm и возвращаем в UI
                        intentBack.putExtra(staticParam.RES_EXC, staticParam.R_ERR);
                        e.printStackTrace();
                    }

                }
                else {
                    //упаковываем в интент сообщение от сервера и возвращаем в UI
                    intentBack.putExtra(staticParam.RES_EXC, staticParam.NET_ERR);
                    intentBack.putExtra(staticParam.ERR_DESC, response.code());
                    Log.e(TAG, "problem: loadWeather response");
                }

                //возвращаемся в UI
                broadcastManager.sendBroadcast(intentBack);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "loadWeather failure");

                //упаковываем в интент сообщение об ошибке и возвращаем в UI
                Intent intentBack = new Intent(RESULT);
                intentBack.putExtra(staticParam.RES_EXC, staticParam.FAILURE);
                broadcastManager.sendBroadcast(intentBack);
            }
        });

        //остановим службу
        stopSelf();
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


    @Override
    public void onDestroy(){
        Log.e(TAG, "service done");
        super.onDestroy();
    }

}
