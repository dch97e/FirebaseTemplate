package com.example.firebasetemplate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firebasetemplate.databinding.FragmentSignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class SignInFragment extends AppFragment {

    private FragmentSignInBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentSignInBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.signInProgressBar.setVisibility(View.GONE);

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build());

        firebaseAuthWithGoogle(GoogleSignIn.getLastSignedInAccount(requireContext()));

        binding.googleSignIn.setOnClickListener(view1 -> signInClient.launch(googleSignInClient.getSignInIntent()));

        binding.emailSignIn.setOnClickListener(v -> FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(
                        binding.email.getText().toString(),
                        binding.password.getText().toString()
                ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                navController.navigate(R.id.action_signInFragment_to_postsHomeFragment);
            } else {
                Toast.makeText(requireContext(), task.getException().getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }));

        binding.goToRegister.setOnClickListener(v -> navController.navigate(R.id.action_signInFragment_to_registerFragment));
    }

    ActivityResultLauncher<Intent> signInClient = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        firebaseAuthWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult(ApiException.class));
                    } catch (ApiException e) {

                    }
                }
            });

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        if(account == null) return;

        binding.signInProgressBar.setVisibility(View.VISIBLE);
        binding.signInForm.setVisibility(View.GONE);

        FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        navController.navigate(R.id.action_signInFragment_to_postsHomeFragment);
                    } else {
                        binding.signInProgressBar.setVisibility(View.GONE);
                        binding.signInForm.setVisibility(View.VISIBLE);
                    }
                });
    }
}