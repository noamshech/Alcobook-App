package com.example.alcobook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.alcobook.model.entity.Post;
import com.example.alcobook.model.helper.PostHelper;

import java.util.List;

public class ProfileViewModel extends ViewModel {

    LiveData<List<Post>> liveData;

    public LiveData<List<Post>> getData(){
        if(liveData==null){
            liveData= PostHelper.getCurrentUserPosts();
        }
        return liveData;
    }
}
