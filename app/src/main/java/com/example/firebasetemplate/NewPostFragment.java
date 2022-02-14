package com.example.firebasetemplate;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentNewPostBinding;
import com.example.firebasetemplate.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDateTime;
import java.util.UUID;

public class NewPostFragment extends AppFragment {

    private FragmentNewPostBinding binding;
    private Uri uriImagen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentNewPostBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.previsualizacion.setOnClickListener(v -> galeria.launch("image/*"));

        appViewModel.uriImagenSeleccionada.observe(getViewLifecycleOwner(), uri -> {
            Glide.with(this).load(uri).into(binding.previsualizacion);
            uriImagen = uri;
        });

        binding.publicar.setOnClickListener(v -> {
            binding.publicar.setEnabled(false);

            FirebaseStorage.getInstance()
                    .getReference("/images/"+ UUID.randomUUID()+".jpg")
                    .putFile(uriImagen)
                    .continueWithTask(task -> task.getResult().getStorage().getDownloadUrl())
                    .addOnSuccessListener(urlDescarga -> {
                        Post post = new Post();
                        post.content = binding.contenido.getText().toString();
                        post.authorName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        post.date = LocalDateTime.now().toString();
                        post.imageUrl = urlDescarga.toString();

                        FirebaseFirestore.getInstance().collection("posts")
                                .add(post)
                                .addOnCompleteListener(task -> {
                                    binding.publicar.setEnabled(true);
                                    navController.popBackStack();
                                });
                    });


        });
    }

    private final ActivityResultLauncher<String> galeria = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        appViewModel.setUriImagenSeleccionada(uri);
    });
}