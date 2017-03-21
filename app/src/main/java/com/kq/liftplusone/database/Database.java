package com.kq.liftplusone.database;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.List;

public interface Database<T> {
    Gson gson = new Gson();

    List<T> getAll();
    T get(String s);
    void add(T t);
    void update(T t);
    void remove(String s);
}
