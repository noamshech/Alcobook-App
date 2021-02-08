package com.example.alcobook.model.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Post implements Serializable {
    @PrimaryKey
    @NonNull
    protected String id;
    @NonNull
    protected String username;
    @NonNull
    protected String imgUrl;
    @NonNull
    protected String text;
    protected long lastUpdated;
    protected boolean isDeleted;

    public Post(@NonNull String id, @NonNull String username, @NonNull String imgUrl,
                @NonNull String text, long lastUpdated, boolean isDeleted){
        this.id=id;
        this.username=username;
        this.imgUrl=imgUrl;
        this.text=text;
        this.lastUpdated=lastUpdated;
        this.isDeleted=isDeleted;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(@NonNull String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }


    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", text='" + text + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
