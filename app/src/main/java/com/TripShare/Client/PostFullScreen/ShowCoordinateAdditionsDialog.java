package com.TripShare.Client.PostFullScreen;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.TripShare.Client.R;


public class ShowCoordinateAdditionsDialog extends AppCompatDialogFragment
{
    private String m_image;
    private String m_note;
    private TextView m_noteTextView;
    private ImageView m_imageView;

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        m_image = getArguments().getString("image");
        m_note = getArguments().getString("note");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_additions, null);

        m_imageView = view.findViewById(R.id.image_view_coordinate);
        m_imageView.setClipToOutline(true);
        m_noteTextView = view.findViewById(R.id.coordinate_note);

        initializeViews();

        builder.setView(view).setTitle("Coordinate Information").setPositiveButton("Done", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });

        return builder.create();
    }

    private void initializeViews()
    {
        if(m_note != null && !m_note.isEmpty())
            m_noteTextView.setText(m_note);

        if(m_image != null)
        {
            byte[] decodedImageString = Base64.decode(m_image, Base64.DEFAULT);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(decodedImageString, 0, decodedImageString.length);
            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmapImage);
            circularBitmapDrawable.setCornerRadius(20);
            m_imageView.setImageDrawable(circularBitmapDrawable);
        }
    }
}