package com.TripShare.Client.Common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import com.TripShare.Client.R;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AddPhotoDialog extends AppCompatDialogFragment {

    private Button m_browseButton;
    private ImageView m_image;
    private float m_currentImageRotation = 0.0f;
    private static final int PICK_IMAGE_REQUEST = 1;  // The request code
    private AttachImageToCoordinateListener m_listenerPostCreation;
    private SendImageToServerAndUpdateProfileViewListener m_listenerProfile;
    private Boolean m_isProfileScreen;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        m_isProfileScreen = getArguments().getBoolean("isProfile");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_add_photo, null);

        m_browseButton = view.findViewById(R.id.buttonBrowse);
        m_image = view.findViewById(R.id.imageView);
        m_browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPhotoFromUser();
            }
        });

        m_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {   // currently, only the View is rotating, because rotating the image takes much more time.
                // Rotation is saved in m_currentImageRotation, when rotation selection is finished and image is about to be uploaded, rotate the bitmap itself and send to the server.

                // Create an animation instance
                float newRotation = m_currentImageRotation + 90f;

                Animation an = new RotateAnimation(m_currentImageRotation, newRotation,
                        m_image.getWidth()/2,
                        m_image.getHeight()/2);

                // Set the animation's parameters
                an.setDuration(500);               // duration in ms
                an.setFillAfter(true);               // keep rotation after animation

                // Apply animation to image view
                m_image.startAnimation(an);

                m_currentImageRotation = newRotation;
            }
        });

        builder.setView(view).setTitle("Upload a photo").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(m_isProfileScreen) {
                    rotateImage(m_currentImageRotation);
                    m_listenerProfile.sendImageToServerAndUpdateView(((BitmapDrawable) m_image.getDrawable()).getBitmap());
                }
                else
                    m_listenerPostCreation.attachImageToRelevantCoordinate(((BitmapDrawable)m_image.getDrawable()).getBitmap());
                dismiss();
            }
        });

        return builder.create();
    }

    private class onAnimationEnd implements Runnable
    {
        @Override
        public void run()
        {
            float currentRotation = m_image.getRotation();
            m_image.setRotation(currentRotation+90.0f);
        }

    }

    private void getPhotoFromUser()
    {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_IMAGE_REQUEST);
    }

    private void rotateImage(float i_degrees) //currently unused method to actually rotate the stored Bitmap, this will be used when uploading the image
    {
        BitmapDrawable drawable = (BitmapDrawable) m_image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Matrix matrix = new Matrix();

        matrix.postRotate(i_degrees);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        m_image.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage); //here we take the image selected
                m_image.setImageBitmap(bitmap);  //and pass the image to the ImageView
                //ApplicationManager.setDrawerProfilePicture(bitmap); // THE FUCK IS THIS?????????
                if(m_isProfileScreen)
                    ApplicationManager.setDrawerProfilePicture(bitmap);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            if(getArguments().getBoolean("isProfile"))
                m_listenerProfile = (SendImageToServerAndUpdateProfileViewListener)context;
            else
                m_listenerPostCreation = (AttachImageToCoordinateListener)context;
        }
        catch (ClassCastException  e)
        {
            throw new ClassCastException("Could not cast to AttachImageToCoordinateListener");
        }
    }

    public interface AttachImageToCoordinateListener
    {
        void attachImageToRelevantCoordinate(Bitmap i_imageToAttach);
    }

    public interface SendImageToServerAndUpdateProfileViewListener
    {
        void sendImageToServerAndUpdateView(Bitmap i_imageToAttach);
    }
}