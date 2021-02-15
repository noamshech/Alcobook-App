package com.example.alcobook.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alcobook.R;
import com.example.alcobook.activity.MainActivity;
import com.example.alcobook.model.entity.Post;
import com.example.alcobook.model.helper.AuthHelper;
import com.squareup.picasso.Picasso;

public class ViewPostFragment extends Fragment {

    final static int EDIT_MENU_ID = 1;

    protected Post mPost;
    protected ImageView mImageView;
    protected TextView mPostedByTv;
    protected TextView mTextTv;


    public ViewPostFragment() {
        // Required empty public constructor
    }

    public static ViewPostFragment newInstance() {
        return new ViewPostFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);

        mImageView = view.findViewById(R.id.view_post_img);
        mPostedByTv = view.findViewById(R.id.view_post_postedby_tv);
        mTextTv = view.findViewById(R.id.view_post_text_tv);

        mPost = ViewPostFragmentArgs.fromBundle(getArguments()).getPost();
        if (mPost != null) {
            updateDisplay();
        }

        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setActionBarTitle("");

        return view;
    }

    protected void updateDisplay() {
        mPostedByTv.setText("Posted by: " + mPost.getUsername());
        mTextTv.setText(mPost.getText());
        if (!mPost.getImgUrl().equals("")) {
            Picasso.get().load(mPost.getImgUrl()).placeholder(R.drawable.placeholder).into(mImageView);
        } else {
            mImageView.setImageResource(R.drawable.placeholder);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (AuthHelper.getUsernameFromPref().equals(mPost.getUsername())) {
            menu.add(Menu.NONE, EDIT_MENU_ID, Menu.NONE, "Edit").setIcon(R.drawable.ic_baseline_edit_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == EDIT_MENU_ID) {
            NavController navCtrl = Navigation.findNavController(ViewPostFragment.this.getActivity().findViewById(R.id.main_nav_host));
            ViewPostFragmentDirections.ActionViewPostFragmentToEditPostFragment direction =
                    ViewPostFragmentDirections.actionViewPostFragmentToEditPostFragment(mPost);
            navCtrl.navigate(direction);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}