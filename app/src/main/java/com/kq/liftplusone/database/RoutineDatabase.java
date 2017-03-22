package com.kq.liftplusone.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import static com.kq.liftplusone.helpers.Constants.*;


public class RoutineDatabase extends SQLiteOpenHelper implements Database<Routine> {

    private final String TABLE_NAME = "ROUTINES";
    private final String KEY_NAME = "name";
    private final String KEY_OBJECT = "routine";

    public RoutineDatabase(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ROUTINE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_NAME + " TEXT PRIMARY KEY,"
                + KEY_OBJECT + " TEXT" + ")";
        db.execSQL(CREATE_ROUTINE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Routine> getAll() {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Routine> routineList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Routine routine = gson.fromJson(cursor.getString(1), Routine.class);
                routineList.add(routine);
            } while (cursor.moveToNext());
        }
        db.close();
        return routineList;
    }

    public Routine get(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME +
                " WHERE " + KEY_NAME + " = ?",
                new String[]{name});

        if (cursor != null)
            cursor.moveToFirst();

        Routine routine = gson.fromJson(cursor.getString(1), Routine.class);
        Log.d(ROUTINE_DATABASE_HELPER_LOG_TAG, "GET -> " + routine.getRoutineName());

        db.close();

        return routine;
    }

    public void add(Routine routine) {
        SQLiteDatabase db = getWritableDatabase();

        String gsonString = gson.toJson(routine);
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, routine.getRoutineName());
        values.put(KEY_OBJECT, gsonString);

        db.insert(TABLE_NAME, null, values);
        db.close();

        Log.d(ROUTINE_DATABASE_HELPER_LOG_TAG, "ADD -> " + routine.getRoutineName());
    }

    public void update(Routine routine) {
        SQLiteDatabase db = getWritableDatabase();

        String gsonString = gson.toJson(routine);
        ContentValues values = new ContentValues();
        values.put(KEY_OBJECT, gsonString);

        db.update(TABLE_NAME, // table
                values, // content
                KEY_NAME + " = ?", // where clause
                new String[]{routine.getRoutineName()}); // where parameters
        db.close();

        Log.d(ROUTINE_DATABASE_HELPER_LOG_TAG, "UPDATE -> " + routine.getRoutineName());
    }

    public void remove(String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME,
                KEY_NAME + " = ?", // where clause
                new String[]{name});
        db.close();

        Log.d(ROUTINE_DATABASE_HELPER_LOG_TAG, "DELETE -> " + name);
    }

    public void dropTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
