package android.example.gpatrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

    String email;
    EditText passwordReg;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        passwordReg = findViewById(R.id.register_password_edit);
        registerBtn = findViewById(R.id.register_password_btn);

        Intent i = getIntent();
        if(i != null){
            email = i.getStringExtra("email");
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNamePickActivity();
            }
        });
    }

    public void goToNamePickActivity(){
        if(passwordReg.getText().toString().length() > 6){
            Intent myIntent = new Intent(PasswordActivity.this,NameActivity.class);
            myIntent.putExtra("email",email);
            myIntent.putExtra("password",passwordReg.getText().toString());
            startActivity(myIntent);
            finish();
        }
        else{
            Toast.makeText(this, "Password length more than 6", Toast.LENGTH_SHORT).show();
        }
    }
}
