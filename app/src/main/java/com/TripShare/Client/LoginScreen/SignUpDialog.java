package com.TripShare.Client.LoginScreen;

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

public class SignUpDialog extends AppCompatDialogFragment
{
    private EditText m_userName;
    private EditText m_password;
    private SendUserSignUpInfoListener m_listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_user_signup, null);
        m_userName = view.findViewById(R.id.username_text);
        m_password = view.findViewById(R.id.password_text);

        builder.setView(view).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Sign Up", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String userName = m_userName.getText().toString();
                String password = m_password.getText().toString();

                m_listener.sendUserToServer(userName, password);
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
            m_listener = (SendUserSignUpInfoListener)context;
        }
        catch (ClassCastException  e)
        {
            throw new ClassCastException("Could not cast to SendUserSignUpInfoListener");
        }
    }

    public interface SendUserSignUpInfoListener
    {
        void sendUserToServer(String i_userName, String i_password);
    }
}