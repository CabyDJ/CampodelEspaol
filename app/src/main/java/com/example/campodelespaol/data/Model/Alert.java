package com.example.campodelespaol.data.Model;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.example.campodelespaol.MainActivity;
import com.example.campodelespaol.R;

public class Alert extends DialogFragment {

    private final Activity mActivity;

    public Alert(Activity act){
        this.mActivity = act;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.permisos)
                .setPositiveButton(R.string.Continuar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // OK

                        // Solicitar permiso
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    }
                })
                /*.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })*/;
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

