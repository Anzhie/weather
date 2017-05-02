package ru.khasanova.weatherhh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmObject;
import ru.khasanova.weatherhh.data.base.City;

public class CityActivity extends AppCompatActivity {
    Realm realm;
    private static final String CITY_KEY = "city_key";

    String cityName;

    @BindView(R.id.currentCityName)
    TextView currentCityName;

    @BindView(R.id.currentWeatherImg)
    ImageView currentWeatherImg;

    @BindView(R.id.currentTemperature)
    TextView currentTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        //для инициализации элементов интерфейса
        ButterKnife.bind(this);

        //получаем параметр - город, для которого надо отобразить погоду
        cityName = getIntent().getStringExtra(CITY_KEY);

        //достаем данные из БД по конкретному городу
        RealmObject currentCity = realm.where(City.class).equalTo("name", cityName).findFirst();
        //CityTable city               = currentCity.getClass();

        //currentCityName.setText(.getName());
        //currentWeatherImg;
        //currentTemperature.setText(.getTemperature());
    }
}
