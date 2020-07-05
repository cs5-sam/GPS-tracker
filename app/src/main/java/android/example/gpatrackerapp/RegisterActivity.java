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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {

    EditText emailReg;
    Button registerEmailBtn;
    ProgressDialog loadingBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        emailReg = findViewById(R.id.register_email_edit);
        registerEmailBtn = findViewById(R.id.register_email_btn);

        registerEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPasswordActivity();
            }
        });
    }

    public void goToPasswordActivity(){

        loadingBar.setTitle("Please wait");
        loadingBar.setMessage("Checking email");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        auth.fetchSignInMethodsForEmail(emailReg.getText().toString())
        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    boolean check = !task.getResult().getSignInMethods().isEmpty();
                    
                    if(!check){
                        Intent i = new Intent(RegisterActivity.this,PasswordActivity.class);
                        i.putExtra("email",emailReg.getText().toString());
                        startActivity(i);
                        finish();
                    }
                    else{
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Email already registered!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
