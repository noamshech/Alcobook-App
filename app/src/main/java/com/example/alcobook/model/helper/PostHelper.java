package com.example.alcobook.model.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.alcobook.GlobalContextApplication;
import com.example.alcobook.model.entity.Post;
import com.example.alcobook.model.firebase.PostFb;
import com.example.alcobook.model.localdb.AppLocalDb;

import java.util.List;
import java.util.function.Consumer;

public class PostHelper {

    private static final String TAG = "PostHelper";

    public static void addPost(Bitmap bitmap, final String text, final Runnable onSuccess, final Consumer<String> onFailure) {
        PostFb.uploadPost(bitmap, text, onSuccess, onFailure);
    }

    public static void editPost(final Post post, Bitmap image, final String text, final Runnable onSuccess, final Consumer<String> onFailure){
        if (image == null && post.getText().equals(text)){
            onSuccess.run();
            return;
        }

        if (image != null){
            PostFb.uploadImageToStorage(image, null, url -> {
                if (!post.getText().equals(text)){
                    PostFb.updatePost(post.getId(),url,text,onSuccess,onFailure);
                } else {
                    PostFb.updatePost(post.getId(),url,null,onSuccess,onFailure);
                }
            }, onFailure);
        } else {
            PostFb.updatePost(post.getId(),null,text,onSuccess,onFailure);
        }
    }

    public static void deletePost(final Post post, final Runnable onSuccess, final Consumer<String> onFailure){
        PostFb.deletePost(post.getId(),onSuccess,onFailure);
    }

    public static LiveData<List<Post>> getAllPosts() {
        LiveData<List<Post>> liveData = AppLocalDb.db.postDao().getAllPosts();
        refreshPostList(null);
        return liveData;
    }

    public static void refreshPostList(final Runnable onComplete) {
        long lastUpdated = GlobalContextApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("postsLastUpdated", 0);
        Log.d(TAG, "refreshPostList: Current lastUpdated = " + lastUpdated);
        PostFb.getAllPostsSince(lastUpdated, posts -> {
            new AsyncTask<String, String, String>() {

                @Override
                protected String doInBackground(String... strings) {
                    long lastUpdated = 0;
                    for (Post p : posts) {
                        AppLocalDb.db.postDao().insertAll(p);
                        if (p.getLastUpdated() > lastUpdated) {
                            lastUpdated = p.getLastUpdated();
                        }
                    }
                    GlobalContextApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE).edit()
                            .putLong("postsLastUpdated", lastUpdated)
                            .commit();
                    return "";
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (onComplete != null) onComplete.run();
                }
            }.execute("");
        });
    }

    public static LiveData<List<Post>> getCurrentUserPosts() {
        return AppLocalDb.db.postDao().getUserPosts(AuthHelper.getUsernameFromPref());
    }
}
