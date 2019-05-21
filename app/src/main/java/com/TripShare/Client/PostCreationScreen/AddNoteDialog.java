package com.TripShare.Client.PostCreationScreen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.TripShare.Client.R;

public class AddNoteDialog extends AppCompatDialogFragment
{
    private EditText m_note;
    private AttachNoteToCoordinateListener m_listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_note_dialog, null);
        m_note = view.findViewById(R.id.note_text);

        builder.setView(view).setTitle("Add your note").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String note = m_note.getText().toString();
                m_listener.attachNoteToRelevantCoordinate(note);
                Toast.makeText(getActivity(), note + " saved as note.", Toast.LENGTH_LONG).show();
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
            m_listener = (AttachNoteToCoordinateListener)context;
        }
        catch (ClassCastException  e)
        {
            throw new ClassCastException("Could not cast to AttachNoteToCoordinateListener");
        }
    }

    public interface AttachNoteToCoordinateListener
    {
        void attachNoteToRelevantCoordinate(String i_noteToAttach);
    }
}