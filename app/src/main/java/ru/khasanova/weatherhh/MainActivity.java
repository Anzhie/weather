package ru.khasanova.weatherhh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import ru.khasanova.weatherhh.data.base.City;
import ru.khasanova.weatherhh.network.NetService;


public class MainActivity extends AppCompatActivity implements WeatherAdapter.onItemClick{
    private Realm realm;
    RealmResults<City> citiesDB;

    private WeatherAdapter adapter;

    BroadcastReceiver receiver;

    private static final String CITY_KEY = "city_key";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //вспомогательные переменные
        final List<City> cities   = new ArrayList<>();
        boolean load        = false;
        long currentTime    = System.currentTimeMillis();
        long thirtyMinDef   = currentTime - (30*60*1000);

        //используем ButterKnife для инициализации элементов интерфейса
        ButterKnife.bind(this);

        //заполняем recyclerView: расположение элементов, адаптер, декоратор
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //привязываемся к параметру, содержащему список городов и обработчику событий нажатия
        adapter = new WeatherAdapter(cities, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemDivider(this));

        //для получения управления из службы по окончании загрузки и записи в БД
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                citiesDB = getCitiesFromRealm();
                showWeather(cities, citiesDB);
            }
        };

        //БД
        realm = Realm.getInstance(this);
        //все записи БД
        citiesDB = getCitiesFromRealm();
        if (!citiesDB.isEmpty()){
            for (int i = 0; i < citiesDB.size(); i++){
                long cityEntryTime = citiesDB.get(i).getTime();
                //если хотя бы у обной записи в БД время получения более 30 минут, загружаем погоду заново
                if (cityEntryTime<thirtyMinDef){
                    load = true;
                    break;
                }
            }
        }
        else {
            //если записей в БД нет, загружаем погоду
            load = true;
        }

        if (load) {
            //удаляем существующие записи БД
            realm.beginTransaction();
            for (int i = citiesDB.size(); i > 0; i--){
                citiesDB.get(i-1).removeFromRealm();
            }
            realm.commitTransaction();

            //получаем города, для которых надо загрузить погоду
            String groupCities = getCitiesList();

            //загружаем погоду (из строки городов надо убрать последнюю ",")
            loadWeather(groupCities.substring(0, groupCities.length() - 1));
        }
        else {
            //если загрузка не требуется, сразу отображаем погоду
            showWeather(cities, citiesDB);
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();

        //закрываем БД
        realm.close();
    }

    @Override
    public void onItemClick(@NonNull City city){
        //создаем явный интент для открытия активити для просмотра погоды выбранного города
        Intent intent = new Intent(this, CityActivity.class);
        //в параметрах передаем город, для которого нужно отобразить погоду
        intent.putExtra(CITY_KEY, city.getName());
        //запускаем активити с детальным описанием выбранного города
        startActivity(intent);
    }


    private String getCitiesList(){
        //получим список городов, для которых надо грузить погоду
        String[] arrayCities = getResources().getStringArray(R.array.Cities);
        String groupOfCities = "";
        for (String city : arrayCities) {
            groupOfCities = groupOfCities + city + ",";
        }
        return groupOfCities;
    }


    private void loadWeather(String groupCities){
        //для загрузки погода стартуем service
        NetService.start(this, groupCities);
    }

    private void showWeather(List<City> cities, RealmResults<City> citiesDB){
        //помещаем данные из БД в список городов
        for (int i = 0; i < citiesDB.size(); i++){
            cities.add(citiesDB.get(i));
        }
        //сообщаем адаптеру, что данные изменились
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onStart(){
        super.onStart();
        //регистрируем приемник для передачи управления из службы в UI
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(NetService.RESULT));
    }


    @Override
    protected void onStop(){
        //отключаем приемник
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }


    private RealmResults<City> getCitiesFromRealm(){
        //получаем все города из БД
        return realm.where(City.class).findAll();
    }

}
