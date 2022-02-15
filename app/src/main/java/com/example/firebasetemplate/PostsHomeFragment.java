package com.example.firebasetemplate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentPostsBinding;
import com.example.firebasetemplate.databinding.ViewholderPostBinding;
import com.example.firebasetemplate.model.Post;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class PostsHomeFragment extends AppFragment {

    private FragmentPostsBinding binding;
    private List<Post> postsList = new ArrayList<>();
    private PostsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentPostsBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fab.setOnClickListener(v -> navController.navigate(R.id.newPostFragment));

        binding.postsRecyclerView.setAdapter(adapter = new PostsAdapter());

       setQuery().addSnapshotListener((collectionSnapshot, e) -> {
           postsList.clear();
            for (DocumentSnapshot documentSnapshot: collectionSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
                post.postid=documentSnapshot.getId();
                postsList.add(post);
            }
            adapter.notifyDataSetChanged();
        });
    }

    Query setQuery ()
    {
        return db.collection("posts");
    }

    class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ViewholderPostBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Post post = postsList.get(position);
            holder.binding.contenido.setText(postsList.get(position).content);
            holder.binding.autor.setText(postsList.get(position).authorName);
            Glide.with(requireContext()).load(postsList.get(position).imageUrl).into(holder.binding.imagen);

            holder.binding.favorito.setOnClickListener(view -> {
                db.collection("posts").
                        document(post.postid).
                        update("likes."+auth.getUid(), !post.likes.containsValue(auth.getUid())? true : FieldValue.delete());
            });
            holder.binding.favorito.setChecked(post.likes.containsKey(auth.getUid()));
        }

        @Override
        public int getItemCount() {
            return postsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewholderPostBinding binding;
            public ViewHolder(ViewholderPostBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}