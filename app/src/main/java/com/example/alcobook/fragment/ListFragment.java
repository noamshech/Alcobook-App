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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alcobook.R;
import com.example.alcobook.activity.MainActivity;
import com.example.alcobook.model.entity.Post;
import com.example.alcobook.model.helper.PostHelper;
import com.example.alcobook.viewmodel.ListViewModel;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ListFragment extends Fragment {

    protected RecyclerView mRecyclerView;
    protected ListViewModel mViewModel;
    protected LiveData<List<Post>> mLiveData;
    protected List<Post> mData = new LinkedList<>();
    protected ListPostListAdapter mAdapter;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PostHelper.refreshPostList(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = view.findViewById(R.id.list_recyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ListPostListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(position -> {
            NavController navCtrl = Navigation.findNavController(ListFragment.this.getActivity().findViewById(R.id.main_nav_host));
            ListFragmentDirections.ActionListFragmentToViewPostFragment3 direction = ListFragmentDirections.actionListFragmentToViewPostFragment3(mData.get(position));
            navCtrl.navigate(direction);
        });

        mLiveData = mViewModel.getData();
        mLiveData.observe(getViewLifecycleOwner(), posts -> {
            mData = posts;
            mAdapter.notifyDataSetChanged();
        });

        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> {
            mViewModel.refresh(() -> {
                swipeRefresh.setRefreshing(false);
            });
        });
        ((MainActivity)getActivity()).setActionBarTitle("All Posts");
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = new ViewModelProvider(this).get(ListViewModel.class);
    }

    static class PostRowViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        TextView mUsername;
        TextView mText;
        Post mPost;

        public PostRowViewHolder(@NonNull View itemView, final Consumer<Integer> onItemClickListener) {
            super(itemView);
            mImage = itemView.findViewById(R.id.list_row_img);
            mUsername = itemView.findViewById(R.id.list_row_username_tv);
            mText = itemView.findViewById(R.id.list_row_text_tv);
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.accept(position);
                    }
                }
            });
        }

        public void bind(Post post) {
            mUsername.setText(post.getUsername());
            mText.setText(post.getText());
            this.mPost = post;
            if (!post.getImgUrl().equals("")) {
                Picasso.get().load(post.getImgUrl()).placeholder(R.drawable.placeholder)
                        .fit().centerInside().into(mImage);
            } else {
                mImage.setImageResource(R.drawable.placeholder);
            }
        }
    }

    class ListPostListAdapter extends RecyclerView.Adapter<PostRowViewHolder> {
        private Consumer<Integer> mOnItemClickListener;

        void setOnItemClickListener(Consumer<Integer> listener) {
            mOnItemClickListener = listener;
        }


        @NonNull
        @Override
        public PostRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.list_row, parent, false);
            return new PostRowViewHolder(v, mOnItemClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull PostRowViewHolder holder, int position) {
            holder.bind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}