package com.example.routesmanagementscreen;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AddPhotoDialog extends AppCompatDialogFragment {

    private Button m_browseButton;
    private ImageView m_image;
    private float m_currentImageRotation = 0.0f;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_photo_dialog, null);

        m_browseButton = view.findViewById(R.id.buttonBrowse);
        m_image = view.findViewById(R.id.imageView);
        m_browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPhotoFromUser();
            }
        });

        m_image.setOnClickListener(new View.OnClickListener(){
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
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Photo uploaded.", Toast.LENGTH_LONG).show();
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
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
    }

    private void rotateImage90Degrees() //currently unused method to actually rotate the stored Bitmap, this will be used when uploading the image
    {
        BitmapDrawable drawable = (BitmapDrawable) m_image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        m_image.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage); //here we take the image selected
                m_image.setImageBitmap(bitmap);  //and pass the image to the ImageView

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}