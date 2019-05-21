package com.TripShare.Client.RoutesScreen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.TripShare.Client.R;

public class EditRouteDialog extends AppCompatDialogFragment
{
    private EditText m_routeNameEditText;
    private EditDialogListener m_listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater =  getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_edit_route_dialog, null);
        m_routeNameEditText = view.findViewById(R.id.newRouteName);

        if (getArguments() != null)
            m_routeNameEditText.setText(getArguments().getString("currentRouteName"));

        builder.setView(view).setTitle("Edit Route Name").setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String newRouteName = m_routeNameEditText.getText().toString();
                m_listener.updateListItemName(newRouteName);
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            m_listener = (EditDialogListener)context;
        }
        catch (ClassCastException  e)
        {
            throw new ClassCastException("Could not cast to EditDialogListener");
        }
    }

    public interface EditDialogListener
    {
        void updateListItemName(String i_newRouteName);
    }
}
