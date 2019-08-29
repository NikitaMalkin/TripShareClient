package com.TripShare.Client.HomeScreen;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import com.TripShare.Client.Common.ApplicationManager;
import com.TripShare.Client.Common.TagSelectionDialog;
import com.TripShare.Client.CommunicationWithServer.SendUserTagsToDB;
import com.TripShare.Client.R;

public class FirstLaunchDialog extends AppCompatDialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_first_time_enter_homepage, null);

        ApplicationManager.setHomePageFirstTimeAccessed();

        builder.setView(view).setTitle("Greetings!")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                final DialogFragment editTagsDialog = new TagSelectionDialog();
                editTagsDialog.show(getFragmentManager(), "");


                getFragmentManager().executePendingTransactions();
                ((TagSelectionDialog) editTagsDialog).selectUserPrefferedTags();
                editTagsDialog.getDialog().setOnDismissListener((new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (((TagSelectionDialog) editTagsDialog).getDialogResolvedSuccessfully()) {
                            ApplicationManager.getLoggedInUser().setPreferredTags(((TagSelectionDialog) editTagsDialog).getSelectedTags());
                            SendUserTagsToDB sendToServer = new SendUserTagsToDB(ApplicationManager.getLoggedInUser().getPreferredTags(), ApplicationManager.getLoggedInUser().getID());
                            sendToServer.execute();
                        }
                    }
                }));
            }
        });

        return builder.create();
    }

}
