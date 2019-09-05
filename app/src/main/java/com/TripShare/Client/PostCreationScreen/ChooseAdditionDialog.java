package com.TripShare.Client.PostCreationScreen;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import com.TripShare.Client.Common.AddPhotoDialog;
import com.TripShare.Client.R;

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

    private void openAddNoteDialog()
    {
        AddNoteDialog dialog = new AddNoteDialog();

        dialog.show(getFragmentManager(), "Add Note Dialog");
    }

    private void openAddPhotoDialog()
    {
        AddPhotoDialog dialog = new AddPhotoDialog();
        Bundle args = new Bundle();
        args.putBoolean("isProfile", false);
        dialog.setArguments(args);

        dialog.show(getFragmentManager(), "Add Photo Dialog");
    }
}
