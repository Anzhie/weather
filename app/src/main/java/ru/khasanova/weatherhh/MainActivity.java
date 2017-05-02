package ru.khasanova.weatherhh;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    private WeatherAdapter adapter;

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
        List<City> cities   = new ArrayList<>();
        boolean load        = false;
        long currentTime    = System.currentTimeMillis() / 1000;
        long thirtyMinBef   = currentTime - (30*60);

        //БД
        realm = Realm.getInstance(this);
        //все записи БД
        RealmResults<City> citiesDB = realm.where(City.class).findAll();
        if (!citiesDB.isEmpty()){
            for (int i = 0; i < citiesDB.size(); i++){
                long cityEntryTime = citiesDB.get(i).getTime();
                //если хотя бы у обной записи в БД время получения более 30 минут, загружаем погоду заново
                if (cityEntryTime<thirtyMinBef){
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
            //получаем города, для которых надо загрузить погоду
            String[] arrayCities = getResources().getStringArray(R.array.Cities);
            String groupCities = "";
            for (String city : arrayCities) {
                groupCities = groupCities + city + ",";
            }

            //загружаем погоду (из строки городов надо убрать последнюю ",")
            loadWeather(groupCities.substring(0, groupCities.length() - 1));
        }
        else {
            //если загрузка не требуется, сразу отображаем погоду
            showWeather(cities, citiesDB);
        }

        //используем ButterKnife для инициализации элементов интерфейса
        ButterKnife.bind(this);

        //заполняем recyclerView: расположение элементов, адаптер, декоратор
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //привязываемся к параметру, содержащему список городов и обработчику событий нажатия
        adapter = new WeatherAdapter(cities, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemDivider(this));
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

        startActivity(intent);
    }


    private void loadWeather(String groupCities){
        //для загрузки погода стартуем service
        NetService.start(this, groupCities);
    }

    private void showWeather(List<City> cities, RealmResults<City> citiesDB){
        //помещаем данные из БД в список городов
        for (int i = 0; i < citiesDB.size(); i++){
            //!!! ИЗМЕНИТЬ - надо вывести картинку погоды для каждого гороа
            cities.add(citiesDB.get(i));
        }
        //сообщаем адаптеру, что данные изменились
        adapter.notifyDataSetChanged();
    }

}
