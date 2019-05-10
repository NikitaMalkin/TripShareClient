package com.example.routesmanagementscreen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

public class ChooseAdditionDialog extends AppCompatDialogFragment
{
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Personalize your trip")
                .setItems(R.array.AdditionTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)
                        {
                            openAddNoteDialog();
                        }
                        else if (which == 1)
                        {
                            openAddPhotoDialog();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    public void openAddNoteDialog()
    {
        AddNoteDialog dialog = new AddNoteDialog();

        dialog.show(getFragmentManager(), "Add Note Dialog");
    }

    public void openAddPhotoDialog()
    {
        AddPhotoDialog dialog = new AddPhotoDialog();

        dialog.show(getFragmentManager(), "Add Photo Dialog");
    }
}
