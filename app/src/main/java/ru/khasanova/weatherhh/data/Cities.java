package ru.khasanova.weatherhh.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;

public class Cities {

    @SerializedName("cnt")
    @Expose
    private Integer cnt;
    @SerializedName("list")
    @Expose
    private java.util.List<ru.khasanova.weatherhh.data.List> list = null;

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public java.util.List<ru.khasanova.weatherhh.data.List> getList() {
        return list;
    }

    public void setList(java.util.List<ru.khasanova.weatherhh.data.List> list) {
        this.list = list;
    }

}
