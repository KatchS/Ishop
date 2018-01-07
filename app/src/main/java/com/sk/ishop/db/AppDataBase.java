package com.sk.ishop.db;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by sk on 13/12/2017.
 */

@Database(entities = {Products.class},version = 1)
public abstract class AppDataBase extends RoomDatabase {

    private final static String DATABASE_NAME = "ProductsDB";

    public abstract ProductsDAO productsDAO();

    private static AppDataBase INSTANCE;

    public static AppDataBase getINSTACE(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(
                    context,
                    AppDataBase.class,
                    DATABASE_NAME
            )
                    .allowMainThreadQueries()
                    .build();
        }

        return INSTANCE;
    }

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
