package com.cz2006.curator.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.cz2006.curator.R;


/**
 * This class contains method to generate alert dialog
 */
public class GenericAlertDialog extends DialogFragment {
    /**
     * This method is to generate an alert dialog
     * @param savedInstanceState
     * @return a alert dialog object
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //0use builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_generic_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        return builder.create();
    }



}
