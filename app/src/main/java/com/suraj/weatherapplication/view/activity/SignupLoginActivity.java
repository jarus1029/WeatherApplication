package com.suraj.weatherapplication.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.suraj.weatherapplication.R;
import com.suraj.weatherapplication.databinding.ActivityMainBinding;
//import com.google.firebase.database.FirebaseDatabase;

public class SignupLoginActivity  extends AppCompatActivity {


    EditText edtUsername,edtEmail,edtPassword;
    Button btnSubmit;
    TextView txtLoginInfo;


    boolean isSigningUp=true;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signuploginactivity);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);




        edtUsername=findViewById(R.id.edtUsername);
        edtEmail=findViewById(R.id.edtEmail);
        edtPassword=findViewById(R.id.edtPassword);

        btnSubmit=findViewById(R.id.btnSubmit);
        txtLoginInfo=findViewById(R.id.txtLoginInfo);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(SignupLoginActivity.this,MainActivity.class));
            finish();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);

                if(isSigningUp)
                {
                    if(edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty() || edtUsername.getText().toString().isEmpty())
                    {
                        Snackbar.make(findViewById(android.R.id.content),"Invalid input",Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                }
                else
                {
                    if(edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty() )
                    {
                        Snackbar.make(findViewById(android.R.id.content),"Invalid input",Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(isSigningUp)
                {
                    handleSignUp();
                }
                else
                {
                    handleLogin();
                }
            }
        });

        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSigningUp)
                {
                    isSigningUp=false;
                    edtUsername.setVisibility(View.GONE);
                    btnSubmit.setText("Log In");
                    txtLoginInfo.setText("Don't have an account? Sign Up");
                }
                else
                {
                    isSigningUp=true;
                    btnSubmit.setText("Sign Up");
                    edtUsername.setVisibility(View.VISIBLE);
                    txtLoginInfo.setText("Already have an account? Log In");
                }
            }
        });

    }

    private void handleLogin()
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Logged in successfully",Snackbar.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupLoginActivity.this,MainActivity.class));

                }
                else
                {
                    Snackbar.make(findViewById(android.R.id.content),task.getException().getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleSignUp()
    {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Signed up successfully",Snackbar.LENGTH_SHORT).show();
//                    FirebaseD.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtUsername.getText().toString(),edtEmail.getText().toString(),""));
                    startActivity(new Intent(SignupLoginActivity.this,MainActivity.class));
                }
                else
                {
                    Snackbar.make(findViewById(android.R.id.content),task.getException().getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}

