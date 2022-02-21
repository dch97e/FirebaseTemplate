package com.example.firebasetemplate;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class AppViewModel extends AndroidViewModel {
    public AppViewModel(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<Uri> uriImagenPerfilSeleccionada = new MutableLiveData<>();

    public MutableLiveData<Uri> uriImagenSeleccionada = new MutableLiveData<>();

    public void setUriImagenSeleccionada(Uri uri) {
        uriImagenSeleccionada.setValue(uri);
    }
    public void setUriImagenPerfilSeleccionada(Uri uri) {
        uriImagenPerfilSeleccionada.setValue(uri);
    }
}