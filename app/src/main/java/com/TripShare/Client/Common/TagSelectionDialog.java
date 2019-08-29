package com.TripShare.Client.Common;

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
import com.TripShare.Client.RoutesScreen.EditRouteDialog;

import java.util.ArrayList;
import java.util.List;

public class TagSelectionDialog extends AppCompatDialogFragment {

    private ArrayList<String> m_selectedTags = new ArrayList<String>();
    private ArrayList<CheckBox> m_checkBoxes = new ArrayList<CheckBox>();
    private boolean m_dialogResolvedSuccessfully = false;
    private View m_view;

    public ArrayList<String> getSelectedTags()
    {
        return m_selectedTags;
    }

    public boolean getDialogResolvedSuccessfully()
    {
        return m_dialogResolvedSuccessfully;
    }

    public void selectUserPrefferedTags()
    {
        User user = ApplicationManager.getLoggedInUser();


        TableLayout tableLayout = m_view.findViewById(R.id.tag_selection_dialog_tableLayout);

        for (int i=0; i<m_checkBoxes.size(); i++)
        {
            String currentCheckboxText = m_checkBoxes.get(i).getText().toString();

            if (user.getPreferredTags().contains(currentCheckboxText)) //check if user's chosen tags contain the current checkbox
            {
                ArrayList<View> checkbox = new ArrayList<>();
                tableLayout.findViewsWithText(checkbox, currentCheckboxText, View.FIND_VIEWS_WITH_TEXT);
                ((CheckBox)checkbox.get(0)).setChecked(true);
            }
        }

    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_tag_selection, null);
        m_view = view;

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

                for (int i=0; i<m_checkBoxes.size(); i++) //when dialog is resolved, we go over all checkboxes, and for each 'checked' checkbox, we add it to the string array
                {
                    if (m_checkBoxes.get(i).isChecked() == true)
                    {
                        m_selectedTags.add(m_checkBoxes.get(i).getText().toString());
                    }
                }
                m_dialogResolvedSuccessfully = true;
                dismiss();
            }
        });

        inflateTableLayout(view);

        return builder.create();
    }


    private void inflateTableLayout(View i_currentView)
    {

        ArrayList<String> labels = ApplicationManager.getTagList();
        m_checkBoxes = generateCheckboxList(labels);
        TableLayout tableLayout = i_currentView.findViewById(R.id.tag_selection_dialog_tableLayout);


        for (int i=0; i<m_checkBoxes.size(); i=i+2)
        {
            TableRow tableRow = new TableRow(getActivity());

            tableRow.addView(m_checkBoxes.get(i));
            tableRow.addView(m_checkBoxes.get(i+1));


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
