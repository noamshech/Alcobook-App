package com.example.alcobook.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
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
import com.example.alcobook.model.helper.PostHelper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.function.Consumer;


public class AddPostFragment extends Fragment {
    private static final int RESULT_LOAD_IMG = 1;

    private static final String TAG = "AddPostFragment";

    private ImageView mImageView;
    private Button mResetBtn;
    private Button mSubmitBtn;
    private EditText mPostEt;
    private ProgressBar mProgressBar;

    private Bitmap mCurrentBitMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        mImageView = view.findViewById(R.id.add_post_img);
        mResetBtn=view.findViewById(R.id.add_post_reset);
        mPostEt=view.findViewById(R.id.add_post_tv);
        mSubmitBtn=view.findViewById(R.id.add_post_update);
        mProgressBar=view.findViewById(R.id.add_post_progressBar);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageFromGallery();
            }
        });
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        ((MainActivity)getActivity()).setActionBarTitle("");
        return view;

    }

    private void submit() {

        String text=mPostEt.getText().toString().trim();
        if (mCurrentBitMap==null){
            Toast.makeText(getContext(), "Please choose an image", Toast.LENGTH_SHORT).show();
            return;
        }
        if (text==null || text.equals("")){
            Toast.makeText(getContext(), "Please enter text", Toast.LENGTH_SHORT).show();
            return;
        }
        mSubmitBtn.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        PostHelper.addPost(mCurrentBitMap, text, () -> {
            Navigation.findNavController(AddPostFragment.this.getActivity()
                    .findViewById(R.id.main_nav_host)).navigate(R.id.listFragment);
            Toast.makeText(GlobalContextApplication.context, "Upload Success", Toast.LENGTH_SHORT).show();
        }, s -> {
            Toast.makeText(AddPostFragment.this.getContext(), s, Toast.LENGTH_SHORT).show();
            mSubmitBtn.setEnabled(true);
            mProgressBar.setVisibility(View.INVISIBLE);
        });
    }

    protected void reset() {
        mPostEt.setText("");
        mImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.placeholder,null));
        mCurrentBitMap=null;
    }

    protected void loadImageFromGallery() {
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
}