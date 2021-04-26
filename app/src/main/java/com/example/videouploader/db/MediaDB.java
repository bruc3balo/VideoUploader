package com.example.videouploader.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.videouploader.models.Models;
import com.example.videouploader.models.Models.Media;

import static com.example.videouploader.models.Models.Media.MEDIA_DB;


@Database(entities = {Media.class}, version = 1, exportSchema = false)
public abstract class MediaDB extends RoomDatabase {

    private static MediaDB instance;

    public abstract MediaDao mediaDao();

    //only 1 instance of db and thread
    static synchronized MediaDB getInstance(Context context) {
        if (instance == null) {
            //use builder due to abstract
            instance = Room.databaseBuilder(context.getApplicationContext(), MediaDB.class, MEDIA_DB)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .allowMainThreadQueries()
                    .build();
            System.out.println("Media Room instance");
        }
        return instance;
    }

    private static final Callback roomCallBack = new Callback() {
        @Override
        public void onCreate(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Handler(Looper.getMainLooper()).post(() -> populateDb(instance));
        }
    };

    private static void populateDb(MediaDB db) {
        MediaDao mediaDao = db.mediaDao();
        System.out.println("Media Database populated");
    }

}
