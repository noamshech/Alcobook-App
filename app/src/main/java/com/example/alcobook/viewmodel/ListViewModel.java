package com.example.alcobook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.alcobook.model.entity.Post;
import com.example.alcobook.model.helper.PostHelper;

import java.util.List;

public class ListViewModel extends ViewModel {
    LiveData<List<Post>> liveData;

    public LiveData<List<Post>> getData(){
        if(liveData==null){
            liveData= PostHelper.getAllPosts();
        }
        return liveData;
    }

    public void refresh(Runnable listener) {
        PostHelper.refreshPostList(listener);
    }
}