package ru.khasanova.weatherhh;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Анжелика on 21.04.2017.
 */

public class WeatherHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.cityName)
    TextView cityName;

    @BindView(R.id.weatherImage)
    ImageView weatherImage;

    public WeatherHolder(View itemView){
        //макет для каждого элемента в RecyclerView
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
