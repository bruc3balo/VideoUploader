package com.example.videouploader.db;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.videouploader.models.Models;
import com.example.videouploader.models.Models.Media;

import java.util.List;

import static com.example.videouploader.models.Models.Media.MEDIA_DB;
import static com.example.videouploader.models.Models.Media.MEDIA_ID;
import static com.example.videouploader.models.Models.User.CREATED_AT;

@Dao
public interface MediaDao {

    String DELETE_MEDIA = "DELETE FROM " + MEDIA_DB;
    String GET_ALL_MEDIA= "SELECT * FROM " + MEDIA_DB + " ORDER BY " + CREATED_AT + " ASC";
    String GET_SPECIFIC_MEDIA = "SELECT * FROM " + MEDIA_DB +" WHERE "+ MEDIA_ID + " LIKE " + ":mediaId "+ " ORDER BY " + CREATED_AT + " ASC";

    @Insert
    void insert(Media media);

    @Update
    void update(Media media);

    @Delete
    void delete(Media media);

    @Query(DELETE_MEDIA)
    void deleteAllMedia();

    @Query(GET_ALL_MEDIA)
    LiveData<List<Media>> getAllMedia();

    @Query(GET_ALL_MEDIA)
    List<Media> getAllMediaList();

    @Query(GET_SPECIFIC_MEDIA)
    Media getSpecificMedia(String mediaId);

}
