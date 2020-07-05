package android.example.gpatrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;

    EditText email,password;
    Button loginBtn;
    ProgressDialog loadingBar;

    String mail, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        email = findViewById(R.id.login_email_edit);
        password = findViewById(R.id.login_password_edit);
        loginBtn = findViewById(R.id.first_login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingBar.setTitle("Login Account");
                loadingBar.setMessage("Please wait, checking credentials");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                login();
            }
        });
    }

    public void login(){

        mail = email.getText().toString();
        pass = password.getText().toString();

        auth.signInWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "User logged in!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this,UserLocationMainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
