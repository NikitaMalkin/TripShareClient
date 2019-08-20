package com.TripShare.Client.HomeScreen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.TripShare.Client.Common.ApplicationManager;
import com.TripShare.Client.R;

import java.util.ArrayList;
import java.util.List;

public class TagSelectionDialog extends AppCompatDialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_tag_selection, null);

        ApplicationManager.setHomePageFirstTimeAccessed();

        builder.setView(view).setTitle("Edit your tags").setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        }).setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dismiss();
            }
        });

        inflateTableLayout(view);

        return builder.create();
    }

    private void inflateTableLayout(View i_currentView)
    {

        ArrayList<String> labels = ApplicationManager.getTagList();
        ArrayList<CheckBox> checkboxes = generateCheckboxList(labels);
        TableLayout tableLayout = i_currentView.findViewById(R.id.tag_selection_dialog_tableLayout);


        for (int i=0; i<checkboxes.size(); i=i+2)
        {
            TableRow tableRow = new TableRow(getActivity());

            tableRow.addView(checkboxes.get(i));
            tableRow.addView(checkboxes.get(i+1));


            tableRow.setPaddingRelative(64, 32, 64, 32);
            tableLayout.addView(tableRow);
        }
    }

    private ArrayList<CheckBox> generateCheckboxList(ArrayList<String> i_labels)
    {
        ArrayList<CheckBox> checkboxes = new ArrayList<>();

        for (int i=0; i<10; i++)
        {
            Context context = getActivity().getApplicationContext();
            CheckBox currentCheckbox = new CheckBox(context);
            currentCheckbox.setText(i_labels.get(i));
            currentCheckbox.setPaddingRelative(64, 32, 64, 32);
            checkboxes.add(currentCheckbox);
        }

        return checkboxes;
    }
}
