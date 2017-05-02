package ru.khasanova.weatherhh.data.base;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

/**
 * Created by Анжелика on 24.04.2017.
 */

//класс для БД
public class City extends RealmObject {
        @Required

        private String name;
        private String temp;
        private String description;
        private long time;


        public City(){}

        public City(String cityName, String cityTemp, String cityImg){
            this.name   = cityName;
            this.temp   = cityTemp;
            this.description  = cityImg;
        }

        public String getName() {
            return name;
        }

        public String getTemp() {
            return temp;
        }

        public String getDescription() {
            return description;
        }

        public long getTime() {
            return time;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setTime(long time) {
            this.time = time;
        }
}

