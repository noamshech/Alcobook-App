package com.example.alcobook.model.localdb;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.alcobook.GlobalContextApplication;
import com.example.alcobook.model.entity.Post;



public class AppLocalDb {
    public static AppLocalDbRepository db=
            Room.databaseBuilder(GlobalContextApplication.context,
                    AppLocalDbRepository.class,
                    "dbAlcobook.db")
                    .fallbackToDestructiveMigration()
                    .build();
}