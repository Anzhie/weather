package ru.khasanova.weatherhh;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.khasanova.weatherhh.data.base.City;

/**
 * Created by Анжелика on 21.04.2017.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder> {

    private final List<City> mCities;
    private final onItemClick mClickListener;

    private static final String CLEAR_STR   = "clear";
    private static final String RAIN_STR    = "rain";
    private static final String SNOW_STR    = "snow";

    private final View.OnClickListener iClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //получаем город из строки, на которую нажал пользователь
            City city = (City) v.getTag();
            //передаем в обработчик, чтобы получить погоду для этого города
            mClickListener.onItemClick(city);
        }
    };


    public WeatherAdapter(@NonNull List<City> cities, @NonNull onItemClick clickListener){
        //получаем список городов и обработчик нажатия на элемент RecyclerView
        mCities         = cities;
        mClickListener  = clickListener;
    }

    @Override
    public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //инициализируем каждый элемент RecyclerView
        View vItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

        return (new WeatherHolder(vItem));
    }

    @Override
    public void onBindViewHolder(WeatherHolder holder, int position){
        //заполняем каждый элемент RecyclerView
        City city = mCities.get(position);
        //город
        holder.cityName.setText(city.getName());

        //картинка
        String imgName = city.getDescription();
        if (imgName.contains(CLEAR_STR)){
            //holder.weatherImage.setIm(Drawable.createFromStream(, CLEAR_STR+".png"));
        }
        else if (imgName.contains(RAIN_STR)){

        }
        else if (imgName.contains(SNOW_STR)){

        }
        else {

        }

        //обработчик события нажатия
        holder.itemView.setOnClickListener(iClickListener);
    }

    @Override
    public int getItemCount(){
        //обязательный - возвращает кол. элементов RecyclerView
        return  mCities.size();
    }


    public interface onItemClick{
        //вызываем обработчик нажатия и передаем город из соответсвующей строки
        void onItemClick(@NonNull City city);
    }
}
