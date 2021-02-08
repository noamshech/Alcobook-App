package com.example.alcobook.model.helper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.alcobook.GlobalContextApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class AuthHelper {
    private final static String TAG="AuthHelper";
    private final static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final static CollectionReference mUsersCollection = FirebaseFirestore.getInstance().collection("users");

    public static void login(String email, String password, final Runnable onSuccess, final Consumer<String> onFailure){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener((task)->{
            if(task.isSuccessful()){
                LoadUsernameToPref();
                onSuccess.run();
            } else {
                String errorMsg= task.getException() != null ? task.getException().getMessage() : "Error unkown";
                onFailure.accept(errorMsg);
            }
        });
    }

    public static void register(final String email, final String password, final String username, final Runnable onSuccess, final Consumer<String> onFailure){
        // We need to check if the username is not taken
        mUsersCollection.whereEqualTo("username",username).get().addOnCompleteListener((task)->{
           if(task.isSuccessful()){
               if(task.getResult()==null){
                   onFailure.accept("Unknown Error");
                   return;
               }
               if(!task.getResult().getDocuments().isEmpty()){
                   onFailure.accept("Username Taken");
                   return;
               }
               mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task1 -> {
                   if(task1.isSuccessful()){
                       Map<String,Object> user=new HashMap<>();
                       user.put("username",username);
                       mUsersCollection.document(mAuth.getUid()).set(user).addOnCompleteListener(task11 -> {
                           if(task11.isSuccessful()){
                               LoadUsernameToPref();
                               onSuccess.run();
                           } else {
                               //Big error! user register without user name!! go to firebase and delete it!
                               onFailure.accept("Error happened when adding username, check DB");
                           }
                       });
                   } else {
                       onFailure.accept(task1.getException().getMessage());
                   }
               });
           }
        });
    }

    private static void LoadUsernameToPref() {
        // This function might create problems if getting username failed;
        DocumentReference docRef=mUsersCollection.document(mAuth.getUid());
        docRef.get().addOnCompleteListener((task)->{
            if (task.isSuccessful()){
                DocumentSnapshot document=task.getResult();
                if(document != null && document.exists()){
                    String username=(String)document.get("username");
                    Log.d(TAG, "LoadUsernameToPref: username is "+username);
                    GlobalContextApplication.context.getSharedPreferences("TAG",Context.MODE_PRIVATE).edit().putString("username",username).commit();
                }
            }
        });
    }


    public static boolean isLoggedIn(){
        return mAuth.getCurrentUser() != null && getUsernameFromPref() != null;
    }

    public static String getUsernameFromPref(){
        return GlobalContextApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE).getString("username",null);
    }

    public static void signout(){
        mAuth.signOut();
        GlobalContextApplication.context.getSharedPreferences("TAG",Context.MODE_PRIVATE).edit().remove("username").commit();

    }
}
