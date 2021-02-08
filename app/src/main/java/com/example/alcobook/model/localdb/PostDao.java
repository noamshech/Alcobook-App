package com.example.alcobook.model.localdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.alcobook.model.entity.Post;

import java.util.List;

@Dao
public interface PostDao {
    @Query("select * from Post where isDeleted=0")
    LiveData<List<Post>> getAllPosts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Post... posts);

    @Query("select * from Post where username=:username and isDeleted=0")
    LiveData<List<Post>> getUserPosts(String username);
}
