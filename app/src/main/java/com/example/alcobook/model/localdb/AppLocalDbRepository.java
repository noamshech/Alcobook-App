package com.example.alcobook.model.localdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.alcobook.model.entity.Post;

@Database(entities = {Post.class}, version = 1)
public abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract PostDao postDao();
}