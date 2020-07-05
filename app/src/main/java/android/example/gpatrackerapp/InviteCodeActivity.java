package android.example.gpatrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class InviteCodeActivity extends AppCompatActivity {

    String name,email,password,date,isSharing,code,userId;
    Uri imageUri;
    Button inviteRegister;

    TextView inviteCode;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    StorageReference storageReference;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        inviteCode = findViewById(R.id.code_text);
        inviteRegister = findViewById(R.id.invite_register_btn);
        auth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("User_Images");

        Intent i = getIntent();
        if(i != null){
            name = i.getStringExtra("name");
            email = i.getStringExtra("email");
            password = i.getStringExtra("password");
            date = i.getStringExtra("date");
            isSharing = i.getStringExtra("isSharing");
            code = i.getStringExtra("code");
            imageUri = i.getParcelableExtra("imageUri");
        }
        inviteCode.setText(code);

        inviteRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void registerUser(){

        loadingBar.setTitle("Creating Account");
        loadingBar.setMessage("Please wait, account being created");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        auth.createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // insert value in RTDB

                    CreateUser createUser = new CreateUser(name,email,password,code,"false","na","na","na");

                    user = auth.getCurrentUser();
                    userId = user.getUid();

                    reference.child(userId).setValue(createUser)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                //save image to firestore
                                StorageReference sr = storageReference.child(user.getUid() + ".jpg");
                                sr.putFile(imageUri)
                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if(task.isSuccessful()){
                                            String downloadImagePath = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                                            reference.child(user.getUid()).child("imageUrl").setValue(downloadImagePath)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){

                                                        loadingBar.dismiss();
                                                        sendVerificationEmail();

                                                        Intent i = new Intent(InviteCodeActivity.this,MainActivity.class);
                                                        startActivity(i);
                                                    }
                                                    else{
                                                        loadingBar.dismiss();
                                                        Toast.makeText(InviteCodeActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(InviteCodeActivity.this, "Not able register user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }

    public void sendVerificationEmail(){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(InviteCodeActivity.this, "Email sent for verification", Toast.LENGTH_SHORT).show();
                            finish();
                            auth.signOut();
                        }
                        else{
                            Toast.makeText(InviteCodeActivity.this, "Could not send email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}