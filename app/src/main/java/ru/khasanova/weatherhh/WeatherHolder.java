package ru.khasanova.weatherhh;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.khasanova.weatherhh.data.base.City;

/**
 * Created by Анжелика on 21.04.2017.
 */

//макет для каждого элемента в RecyclerView
public class WeatherHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.cityName)
    TextView cityName;

    @BindView(R.id.weatherImage)
    ImageView weatherImage;

    public WeatherHolder(View itemView){
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
