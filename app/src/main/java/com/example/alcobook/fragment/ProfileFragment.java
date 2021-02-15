package com.example.alcobook.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alcobook.R;
import com.example.alcobook.activity.MainActivity;
import com.example.alcobook.model.entity.Post;
import com.example.alcobook.model.helper.AuthHelper;
import com.example.alcobook.viewmodel.ProfileViewModel;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ProfileFragment extends Fragment {
    protected RecyclerView mRecyclerView;
    protected ProfileViewModel mViewModel;
    protected LiveData<List<Post>> mLiveData;
    protected List<Post> mData = new LinkedList<>();
    protected ProfilePostListAdapter mAdapter;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mRecyclerView = view.findViewById(R.id.profile_recyclerView);
        mRecyclerView.hasFixedSize();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter= new ProfilePostListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(position -> {
            NavController navCtrl = Navigation.findNavController(ProfileFragment.this.getActivity().findViewById(R.id.main_nav_host));
            ProfileFragmentDirections.ActionProfileFragmentToViewPostFragment direction=
                    ProfileFragmentDirections.actionProfileFragmentToViewPostFragment(mData.get(position));
            navCtrl.navigate(direction);
        });

        mLiveData = mViewModel.getData();
        mLiveData.observe(getViewLifecycleOwner(), posts -> {
            mData = posts;
            mAdapter.notifyDataSetChanged();
        });

        ((MainActivity)getActivity()).setActionBarTitle(AuthHelper.getUsernameFromPref()+"'s Profile");
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    static class PostRowViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        TextView mTextTv;
        Post mPost;

        public PostRowViewHolder(@NonNull View itemView, final Consumer<Integer> onEditClickListener) {
            super(itemView);
            mImage = itemView.findViewById(R.id.profile_row_img);
            mTextTv = itemView.findViewById(R.id.profile_row_text);
            itemView.findViewById(R.id.profile_row_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onEditClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onEditClickListener.accept(position);
                        }
                    }
                }
            });
        }

        public void bind(Post post) {
            mTextTv.setText(post.getText());
            this.mPost = post;
            if (!post.getImgUrl().equals("")) {
                Picasso.get().load(post.getImgUrl()).placeholder(R.drawable.placeholder).centerCrop().fit().into(mImage);
            } else {
                mImage.setImageResource(R.drawable.placeholder);
            }
        }


    }

    class ProfilePostListAdapter extends RecyclerView.Adapter<ProfileFragment.PostRowViewHolder> {
        private Consumer<Integer> onEditClickListener;

        void setOnItemClickListener(Consumer<Integer> listener) {
            onEditClickListener = listener;
        }

        @NonNull
        @Override
        public ProfileFragment.PostRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.profile_row, parent, false);
            return new ProfileFragment.PostRowViewHolder(v, onEditClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ProfileFragment.PostRowViewHolder holder, int position) {
            holder.bind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}