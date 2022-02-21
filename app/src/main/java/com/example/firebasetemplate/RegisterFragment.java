package com.example.firebasetemplate;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;
import java.util.UUID;


public class RegisterFragment extends AppFragment {
    private FragmentRegisterBinding binding;
    private Uri uriImagen;
    private Uri downloadUriImagen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentRegisterBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imageRegister.setOnClickListener(v -> galeria.launch("image/*"));

        appViewModel.uriImagenPerfilSeleccionada.observe(getViewLifecycleOwner(), uri -> {
            Glide.with(this).load(uri).into(binding.imageRegister);
            uriImagen = uri;
        });

        binding.createAccountButton.setOnClickListener(v -> {
            if (binding.nameEditText.getText().toString().isEmpty()) {
                binding.nameEditText.setError("Required name.");
                return;
            }
            if (binding.emailEditText.getText().toString().isEmpty()) {
                binding.emailEditText.setError("Required email.");
                return;
            }
            if (binding.passwordEditText.getText().toString().isEmpty()) {
                binding.passwordEditText.setError("Required password.");
                return;
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.emailEditText.getText().toString(),
                    binding.passwordEditText.getText().toString()
            ).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // registro el usuario
                    // subo la foto y obtengo su url
                    FirebaseStorage.getInstance()
                            .getReference("/images/" + UUID.randomUUID() + ".jpg")
                            .putFile(uriImagen)
                            .continueWithTask(task1 -> task1.getResult().getStorage().getDownloadUrl())
                            .addOnSuccessListener(urlDescarga -> {
                                downloadUriImagen = urlDescarga;
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(binding.nameEditText.getText().toString())
                                        .setPhotoUri(downloadUriImagen)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task12 -> {
                                            if (task12.isSuccessful()) {
                                                navController.navigate(R.id.action_registerFragment_to_postsHomeFragment);
                                                Log.d("asd", "User profile updated.");
                                            }
                                        });
                            });
                } else {
                    Log.d("FAIL", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }


    private final ActivityResultLauncher<String> galeria = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> appViewModel.setUriImagenPerfilSeleccionada(uri));
}