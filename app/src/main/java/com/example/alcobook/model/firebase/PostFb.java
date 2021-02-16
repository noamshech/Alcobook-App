package com.example.alcobook.model.firebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.alcobook.model.entity.Post;
import com.example.alcobook.model.helper.AuthHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PostFb {
    final static String TAG = "PostFb";
    static CollectionReference mPostsCollection = FirebaseFirestore.getInstance().collection("posts");

    public static void uploadPost(Bitmap bitmap, final String text, final Runnable onSuccess, final Consumer<String> onFailure){
        uploadImageToStorage(bitmap,null,
                (url)->{uploadPostToFirestore(url,text,onSuccess,onFailure);},
                onFailure);
    }

    public static void uploadPostToFirestore(final String url, final String text, final Runnable onSuccess, final Consumer<String> onFailure){
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("username", AuthHelper.getUsernameFromPref());
        postMap.put("img_url", url);
        postMap.put("text", text);
        postMap.put("last_updated", FieldValue.serverTimestamp());
        postMap.put("is_deleted", false);
        mPostsCollection.add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful() && task.getResult() != null) {

                    Post p = new Post(task.getResult().getId(), AuthHelper.getUsernameFromPref(),
                            url, text, 0, false);

                    onSuccess.run();
                } else {
                    onFailure.accept("Failed to upload post to DB after uploaded image");
                }
            }
        });
    }

    public static void uploadImageToStorage(Bitmap bitmap, String name, final Consumer<String> onSuccess, final Consumer<String> onFailure){
        // The goal is to upload the image and get back a uri to reference in the DB
        if (name == null){
            name = "Image "+new Date().getTime();
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imagesRef = storage.getReference().child("images").child(name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(e -> onFailure.accept("Image failed to upload"))
                .addOnSuccessListener(taskSnapshot -> imagesRef.getDownloadUrl()
                        .addOnSuccessListener(url -> onSuccess.accept(url.toString())));
    }

    public static void getAllPostsSince(long lastUpdated, final Consumer<List<Post>> listener) {
        Timestamp ts=new Timestamp(lastUpdated,0);

        mPostsCollection.whereGreaterThanOrEqualTo("last_updated",ts).get()
                .addOnCompleteListener(task -> {
                    List<Post> postData = new LinkedList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Log.d(TAG, "getAllPostsSince: "+doc.getId());
                            postData.add(postFactory(doc));
                        }
                    }
                    listener.accept(postData);
                });
    }

    public static void updatePost(String id, String url, String text, final Runnable onSuccess, final Consumer<String> onFailure){
        Map<String,Object> updates=new HashMap<>();
        if(url!=null)
            updates.put("img_url",url);
        if(text!=null)
            updates.put("text",text);
        updatePostInStorage(id,updates,onSuccess,onFailure);
    }

    public static void deletePost(String id, final Runnable onSuccess, final Consumer<String> onFailure){
        Map<String,Object> updates=new HashMap<>();
        updates.put("is_deleted",true);
        updatePostInStorage(id,updates,onSuccess,onFailure);
    }

    protected static void updatePostInStorage(String id, Map<String,Object> updates, final Runnable onSuccess, final Consumer<String> onFailure){
        updates.put("last_updated",FieldValue.serverTimestamp());
        mPostsCollection.document(id).update(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onSuccess.run();
            } else {
                onFailure.accept("Failed updating post in Firebase DB");
            }
        });
    }

    protected static Post postFactory(QueryDocumentSnapshot doc) {
        Map<String, Object> json = doc.getData();
        Timestamp ts = (Timestamp) json.get("last_updated");

        return new Post(doc.getId(),
                (String) json.get("username"),
                (String) json.get("img_url"),
                (String) json.get("text"),
                ts != null ? ts.getSeconds() : 0,
                (boolean) json.get("is_deleted"));
    }
}
