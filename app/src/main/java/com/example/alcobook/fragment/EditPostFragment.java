package com.example.alcobook.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.alcobook.GlobalContextApplication;
import com.example.alcobook.R;
import com.example.alcobook.activity.MainActivity;
import com.example.alcobook.model.entity.Post;
import com.example.alcobook.model.helper.PostHelper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.function.Consumer;

public class EditPostFragment extends Fragment {


    


    private static final int RESULT_LOAD_IMG = 1;
    private static final String TAG = "EditPostFragment";

    protected ImageView mImageView;
    protected Button mDeleteBtn;
    protected Button mUpdateBtn;
    protected EditText mPostEt;
    protected Bitmap mCurrentBitMap;
    protected ProgressBar mProgressBar;

    protected Post mPost;

    public EditPostFragment() {
        // Required empty public constructor
    }

    public static EditPostFragment newInstance(String param1, String param2) {
        EditPostFragment fragment = new EditPostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        mImageView = view.findViewById(R.id.post_edit_img);
        mDeleteBtn=view.findViewById(R.id.post_edit_delete);
        mPostEt=view.findViewById(R.id.post_edit_tv);
        mUpdateBtn=view.findViewById(R.id.post_edit_update);
        mProgressBar=view.findViewById(R.id.post_edit_progressBar);

        mImageView.setOnClickListener(v -> loadImageFromGallery());
        mDeleteBtn.setOnClickListener(v -> delete());
        mUpdateBtn.setOnClickListener((View.OnClickListener) v -> update());

        mPost = EditPostFragmentArgs.fromBundle(getArguments()).getPost();

        loadPostData();
        ((MainActivity)getActivity()).setActionBarTitle("");

        return view;
    }

    private void loadPostData() {
        mPostEt.setText(mPost.getText());
        Picasso.get().load(mPost.getImgUrl()).noPlaceholder().fit().centerInside().into(mImageView);
    }

    private void loadImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK && null != data) {
            try {
                mCurrentBitMap = MediaStore.Images.Media.getBitmap(GlobalContextApplication.context.getContentResolver(), data.getData());
            } catch (IOException e) {
                Log.d(TAG, "onActivityResult: Failed to load bitmap");
            }
            Picasso.get().load(data.getData()).noPlaceholder().fit().centerInside().into(mImageView);
        }
    }

    protected void delete() {
        disableButtonsAndShowProgress(true);
        PostHelper.deletePost(mPost, () -> {
            navigateBackToListFragment();
        }, s -> {
            Toast.makeText(EditPostFragment.this.getContext(), s, Toast.LENGTH_SHORT).show();
            disableButtonsAndShowProgress(false);
        });
    }

    protected void update() {
        final String text=mPostEt.getText().toString().trim();
        if(text.equals("")){
            Toast.makeText(EditPostFragment.this.getContext(), "Enter text", Toast.LENGTH_SHORT).show();
            return;
        }
        disableButtonsAndShowProgress(true);
        PostHelper.editPost(mPost, mCurrentBitMap, text, () -> {
            navigateBackToListFragment();
        }, s -> {
            Toast.makeText(EditPostFragment.this.getContext(), s, Toast.LENGTH_SHORT).show();
            disableButtonsAndShowProgress(false);
        });
    }

    protected void disableButtonsAndShowProgress(boolean disable){
        mUpdateBtn.setEnabled(!disable);
        mDeleteBtn.setEnabled(!disable);
        mProgressBar.setVisibility(disable ? View.VISIBLE : View.INVISIBLE);
    }

    protected void navigateBackToListFragment(){
        Navigation.findNavController(EditPostFragment.this.getActivity().findViewById(R.id.main_nav_host)).popBackStack(R.id.listFragment,false);
    }
}