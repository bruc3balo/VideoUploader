package com.example.videouploader.db;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;


import com.example.videouploader.models.Models;
import com.example.videouploader.models.Models.Media;

import java.util.ArrayList;
import java.util.List;

public class MediaRepository {
    private final MediaDao mediaDao;
    private final LiveData<List<Media>> allMedia;


    public MediaRepository(Application application) {
        MediaDB database = MediaDB.getInstance(application);
        mediaDao = database.mediaDao();
        allMedia = mediaDao.getAllMedia();
    }

    //Abstraction layer for encapsulation
    public void insert(Media media) {
        insertMedia(media);
    }

    public void update(Media media) {
        updateMedia(media);
    }

    public void delete(Media media) {
        deleteMedia(media);
    }

    public void deleteAllMedia() {
        deleteAllTheMedia();
    }

    public LiveData<List<Media>> getAllMedia() {
        return allMedia;
    }

    public List<Media> getAllMediaList () {
        ArrayList<Media> mediaList = new ArrayList<>();
        new Handler(Looper.getMainLooper()).post(() -> mediaList.addAll(mediaDao.getAllMediaList()));
        return mediaList;
    }

    public Media getSpecificMedia (String mediaId) {
        return mediaDao.getSpecificMedia(mediaId);
    }

    private void insertMedia(Media media) {
        new Handler(Looper.myLooper()).post(() -> {
            mediaDao.insert(media);
            System.out.println(media.getMediaId() + " inserted");
        });
    }

    private void updateMedia(Media media) {
        new Handler(Looper.myLooper()).post(() -> {
            mediaDao.update(media);
            System.out.println(media.getMediaId() + " updated");
        });
    }

    private void deleteMedia(Media media) {
        new Handler(Looper.myLooper()).post(() -> {
            mediaDao.delete(media);
            System.out.println(media.getMediaId() + "deleted");
        });
    }

    private void deleteAllTheMedia() {
        new Handler(Looper.myLooper()).post(() -> {
            mediaDao.deleteAllMedia();
            System.out.println("All Media deleted");
        });
    }

}
