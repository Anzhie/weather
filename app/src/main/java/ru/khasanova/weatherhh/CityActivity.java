package ru.khasanova.weatherhh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import ru.khasanova.weatherhh.data.base.City;

import static java.lang.Math.round;

public class CityActivity extends AppCompatActivity {
    Realm realm;
    private static final String CITY_KEY = "city_key";

    private static final String CLEAR_STR   = "clear";
    private static final String RAIN_STR    = "rain";
    private static final String SNOW_STR    = "snow";

    private static final String TAG = "WEATHER_TOKEN";

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //для инициализации элементов интерфейса
        ButterKnife.bind(this);

        //получаем параметр - город, для которого надо отобразить погоду
        cityName = getIntent().getStringExtra(CITY_KEY);

        //достаем данные из БД по конкретному городу
        realm = Realm.getInstance(this);
        City city = realm.where(City.class).equalTo("name", cityName).findFirst();

        //заполняем интерфейс
        currentCityName.setText(city.getName());

        Double tempD = Double.parseDouble(city.getTemp());
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        String temp = numberFormat.format(round(tempD));   //.intValue(

        currentTemperature.setText(temp + "\u00B0C");

        //картинка
        String imgName = city.getDescription();
        if (imgName.contains(CLEAR_STR)){
            currentWeatherImg.setImageResource(R.drawable.clear);
        }
        else if (imgName.contains(RAIN_STR)){
            currentWeatherImg.setImageResource(R.drawable.rain);
        }
        else if (imgName.contains(SNOW_STR)){
            currentWeatherImg.setImageResource(R.drawable.snow);
        }
        else {
            currentWeatherImg.setImageResource(R.drawable.def);
        }
    }

}
