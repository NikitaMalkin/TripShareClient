package com.TripShare.Client.LoginScreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.TripShare.Client.CommunicationWithServer.SendUserToAddToDB;
import com.TripShare.Client.CommunicationWithServer.ValidateUserInfo;
import com.TripShare.Client.R;
import com.TripShare.Client.Common.User;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class LoginScreen extends AppCompatActivity implements ValidateUserInfo.NotifyIfValidInfoListener, SignUpDialog.SendUserSignUpInfoListener, SendUserToAddToDB.SetUserIDIfNotExistListener
{
    private User m_user;
    private EditText m_userName;
    private EditText m_password;
    private Button m_LoginButton;
    private TextView m_signUpLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        getSupportActionBar().hide();

        m_userName = (EditText) findViewById(R.id.Email_Text_Box);
        m_password = (EditText) findViewById(R.id.Password_Text_Box);
        m_LoginButton = (Button) findViewById(R.id.Login_button);
        m_signUpLink = (TextView) findViewById(R.id.Signup_link);
        m_user = new User();

        SpannableString content = new SpannableString("Don't have an account? Sign Up!");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        m_signUpLink.setText(content);

        m_LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(m_userName.getText().toString(), m_password.getText().toString());
            }
        });

        m_signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpDialog signUpDialog = new SignUpDialog();
                signUpDialog.show(getSupportFragmentManager(), "Sign Up");
            }
        });
    }

    private String encrypt(String i_password)
    {
       return Hashing.sha256()
                .hashString(i_password, StandardCharsets.UTF_8)
                .toString();
    }

    private void validate(String i_userName, String i_password)
    {
        new ValidateUserInfo(this, i_userName, encrypt(i_password)).execute();
    }

    @Override
    public void showAppropriateMessage(final boolean i_isValidUsernameAndPassword)
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (i_isValidUsernameAndPassword)
                {
                    Toast.makeText(getApplicationContext(), "Logging In!", Toast.LENGTH_SHORT).show();
                    // Move to next screen .. For Example HomePage
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Wrong Username or Password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void sendUserToServer(String i_userName, String i_password)
    {
        m_user.setUserName(i_userName);
        m_user.setPassword(encrypt(i_password));
        new SendUserToAddToDB(m_user, this).execute();
    }

    @Override
    public void setUserIDIfNotExist(final long i_userID)
    {
        runOnUiThread(new Runnable() {
            public void run() {
                if (i_userID == -1)
                    Toast.makeText(getApplicationContext(), "User Already Exists!", Toast.LENGTH_SHORT).show();
                else
                {
                    m_user.setUserID(i_userID);
                    Toast.makeText(getApplicationContext(), "Signing you up!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
